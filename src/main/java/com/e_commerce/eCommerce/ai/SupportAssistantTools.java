package com.e_commerce.eCommerce.ai;

import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.StoreInfoResponseDTO;
import com.e_commerce.eCommerce.entity.*;
import com.e_commerce.eCommerce.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
@Slf4j
@Component
@AllArgsConstructor
public class SupportAssistantTools {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final VendorRepository vendorRepository;
    private final vendorBussinesss vendorBussinesss;
    private final VendorBrandingRepository vendorBrandingRepository;
    private final VendorAddresss vendorAddresssl;
    @Tool(description = "Search this store's active products by name/keyword and get their price and stock. " +
            "Use this whenever a customer asks if a product is available, how much it costs, or if it's in stock.")
    public List<ProductSearchResult> searchProducts(
            @ToolParam(description = "Keyword or product name to search for, e.g. 'blue t-shirt'") String keyword) {

        String tenantId = TenantContext.getTenantId();
        if (tenantId == null || keyword == null || keyword.isBlank()) {
            return List.of();
        }

        List<Product> products = productRepository
                .findTop5ByTenantIdAndStatusAndProductNameContainingIgnoreCase(
                        tenantId, ProductStatus.ACTIVE, keyword.trim());

        return products.stream()
                .map(p -> new ProductSearchResult(
                        p.getId(),
                        p.getProductName(),
                        p.getSellingPrice(),
                        p.getStockQuantity() != null && p.getStockQuantity() > 0,
                        p.getStockQuantity()))
                .toList();
    }

    @Tool(description = "Look up the current status of a customer's order using the store's order number " +
            "(e.g. 'ORD-2024-00123'). Use this whenever a customer asks where their order is or its status.")
    public OrderStatusResult getOrderStatus(
            @ToolParam(description = "The exact order number given by the customer") String orderNumber) {

        String tenantId = TenantContext.getTenantId();
        if (tenantId == null || orderNumber == null || orderNumber.isBlank()) {
            return OrderStatusResult.notFound();
        }

        Order order = orderRepository.findByTenantIdAndOrderNumber(tenantId, orderNumber.trim());
        if (order == null) {
            return OrderStatusResult.notFound();
        }

        return new OrderStatusResult(
                true,
                order.getOrderNumber(),
                order.getOrderStatus() != null ? order.getOrderStatus().name() : "UNKNOWN",
                order.getPaymentStatus() != null ? order.getPaymentStatus().name() : "UNKNOWN",
                order.getTotalItems(),
                order.getTotal());
    }

    @Tool(description = "Provide general information about the current store/tenant. Use this whenever a customer asks about the store itself, such as the store name, address, phone number, email, business hours, delivery areas, or other basic store information.")
    public StoreInfoResponseDTO getStoreInfo() {
        log.error("Collecting Store Info");

        String tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant ID is missing");
        }

        Vendor vendor = vendorRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new RuntimeException("Vendor does not exist"));
        VendorBusiness vendorBusiness = vendorBussinesss.findByVendorId(vendor.getId());

        VendorBranding branding = vendorBrandingRepository.findByVendorId(vendor.getId());

        StoreInfoResponseDTO dto = new StoreInfoResponseDTO();

        dto.setVendorId(vendor.getId());

        dto.setTenantId(vendor.getTenantId());
        dto.setBusinessName(vendor.getBussinessName());
        dto.setStoreName(vendor.getStoreName());

        if (branding != null) {
            dto.setStoreType(vendorBusiness.getBusinessCategory());
            dto.setTagline(branding.getStoreTagline());
            dto.setAboutUs(branding.getStoreDescription());

            dto.setLogoUrl(branding.getLogoUrl());
            dto.setBannerUrl(branding.getBannerUrl());

            dto.setThemeColor(branding.getPrimaryColor());

            dto.setSupportEmail(branding.getSupportEmail());
            dto.setSupportPhone(branding.getSupportPhone());
        }

        VendorAddress address = vendorAddresssl.findByVendorId(vendor.getId());
        if (address != null) {
            StringBuilder sb = new StringBuilder();

            if (address.getAddressLine1() != null)
                sb.append(address.getAddressLine1());

            if (address.getAddressLine2() != null && !address.getAddressLine2().isBlank())
                sb.append(", ").append(address.getAddressLine2());

            if (address.getCity() != null)
                sb.append(", ").append(address.getCity());

            if (address.getState() != null)
                sb.append(", ").append(address.getState());

            if (address.getCountry() != null)
                sb.append(", ").append(address.getCountry());

            if (address.getPostalCode() != null)
                sb.append(" - ").append(address.getPostalCode());

            dto.setAddress(sb.toString());

        }
        return dto;
    }





    public record ProductSearchResult(
            Long productId,
            String productName,
            java.math.BigDecimal price,
            boolean inStock,
            Integer stockQuantity) {
    }

    public record OrderStatusResult(
            boolean found,
            String orderNumber,
            String orderStatus,
            String paymentStatus,
            Integer totalItems,
            java.math.BigDecimal total) {

        public static OrderStatusResult notFound() {
            return new OrderStatusResult(false, null, null, null, null, null);
        }
    }
}
