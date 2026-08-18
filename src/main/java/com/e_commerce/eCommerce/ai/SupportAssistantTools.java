package com.e_commerce.eCommerce.ai;

import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.entity.Order;
import com.e_commerce.eCommerce.entity.Product;
import com.e_commerce.eCommerce.entity.ProductStatus;
import com.e_commerce.eCommerce.repository.OrderRepository;
import com.e_commerce.eCommerce.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Tools exposed to the support chatbot's LLM. Every tool is deliberately
 * READ-ONLY and scoped to the current tenant (resolved from the request's
 * subdomain via {@link TenantContext}), so the bot can never see or leak
 * another store's data and can never mutate anything.
 */
@Component
@AllArgsConstructor
public class SupportAssistantTools {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

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
