package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.config.R2Properties;
import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.*;
import com.e_commerce.eCommerce.entity.*;
import com.e_commerce.eCommerce.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDashBoardService {

    private final VendorBrandingRepository vendorBrandingRepository;
    private final VendorRepository vendorRepository;
    private final VendorAddresss vendorAddresssRepo;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final R2Properties r2Properties;
    private final PasswordEncoder passwordEncoder;
    private final vendorBussinesss vendorBussinesss;
    private final UserRepos userRepos;
    private final BannerRepository bannerRepository;
    private final EmailSubscriberRepository newsletterSubscriberRepository;

    public StoreInfoResponseDTO getStoreInfo() {

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

        VendorAddress address = vendorAddresssRepo.findByVendorId(vendor.getId());
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

    public List<CategoryResponseDTO> getActiveCategory() {
        String tenantId = TenantContext.getTenantId();

        List<ProductCategory> categories =
                categoryRepository.findAllByTenantIdAndStatus(
                        tenantId,
                        CategoryStatus.ACTIVE
                );

        return categories.stream()
                .map(category -> {
                    CategoryResponseDTO dto = new CategoryResponseDTO();
                    dto.setId(category.getId());
                    dto.setCategoryName(category.getCategoryName());
                    dto.setImageUrl(category.getImageUrl());
                    return dto;
                })
                .toList();
    }

    public List<ProductCardResponseDTO> getFeaturedProd(String tenant) {
        Optional<Vendor> vendor = vendorRepository.findByTenantId(tenant);
        List<ProductCardResponseDTO> productCardResponseDTOS = new ArrayList<>();
        if (vendor.isEmpty()) {
            throw new RuntimeException("Vendor Does not exist");
        }
        Vendor v1 = vendor.get();

        List<Product> products = productRepository.findAllByTenantIdAndStatusAndFeatured(tenant, ProductStatus.ACTIVE, true);
        for (Product product : products) {
            ProductCardResponseDTO productCardResponseDTO = new ProductCardResponseDTO();
            productCardResponseDTO.setProductId(product.getId());
            productCardResponseDTO.setName(product.getProductName());
            productCardResponseDTO.setBusinessName(v1.getBussinessName());

            productCardResponseDTO.setImage(r2Properties.getPublicUrl() + "/" + product.getProductImage());
            productCardResponseDTO.setBrand(v1.getStoreName());
            productCardResponseDTO.setRating(4.4);
            productCardResponseDTO.setReviewCount(1200);
            productCardResponseDTO.setPrice(product.getSellingPrice());
            productCardResponseDTO.setOldPrice(product.getMrp());
            productCardResponseDTO.setDiscountPercent(getDiscountPrice(product.getSellingPrice(), product.getMrp()));
            productCardResponseDTO.setDeliveryEta("0-1 Days");
            productCardResponseDTO.setVendorId(vendor.get().getId());
            String stockLabel = "";
            if (product.getStockQuantity() > 1) {
                stockLabel = "low_stock";
                productCardResponseDTO.setStockLevel(stockLabel);

            } else {
                stockLabel = "out_of_stock";
                productCardResponseDTO.setStockLevel(stockLabel);
            }
            productCardResponseDTOS.add(productCardResponseDTO);

        }
        return productCardResponseDTOS;
    }


    private Integer getDiscountPrice(BigDecimal sellingPrice, BigDecimal mrp) {

        if (sellingPrice == null || mrp == null || mrp.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }

        BigDecimal discount = mrp.subtract(sellingPrice)
                .multiply(BigDecimal.valueOf(100))
                .divide(mrp, 2, RoundingMode.HALF_UP);

        return discount.intValue();
    }


    @Cacheable(value = "products", key = "T(com.e_commerce.eCommerce.config.TenantContext).getTenantId()")
    public List<ProductCardResponseDTO> getAllProducts(String tenant) {
        log.error("DB hit for gtAll Products");
        Optional<Vendor> vendor = vendorRepository.findByTenantId(tenant);
        List<ProductCardResponseDTO> productCardResponseDTOS = new ArrayList<>();
        if (vendor.isEmpty()) {
            throw new RuntimeException("Vendor Does not exist");
        }
        Vendor v1 = vendor.get();
        List<Product> products = productRepository.findAllByTenantIdAndStatus(tenant, ProductStatus.ACTIVE);

        for (Product product : products) {
            ProductCardResponseDTO productCardResponseDTO = new ProductCardResponseDTO();
            productCardResponseDTO.setProductId(product.getId());
            productCardResponseDTO.setName(product.getProductName());
            productCardResponseDTO.setBusinessName(v1.getBussinessName());
            productCardResponseDTO.setImage(r2Properties.getPublicUrl() + "/" + product.getProductImage());
            productCardResponseDTO.setBrand(v1.getStoreName());
            productCardResponseDTO.setRating(4.4);
            productCardResponseDTO.setReviewCount(1200);
            productCardResponseDTO.setPrice(product.getSellingPrice());
            productCardResponseDTO.setOldPrice(product.getMrp());
            productCardResponseDTO.setDiscountPercent(getDiscountPrice(product.getSellingPrice(), product.getMrp()));
            productCardResponseDTO.setDeliveryEta("0-1 Days");
            String stockLabel = "";
            if (product.getStockQuantity() > 1) {
                stockLabel = "low_stock";
                productCardResponseDTO.setStockLevel(stockLabel);

            } else {
                stockLabel = "out_of_stock";
                productCardResponseDTO.setStockLevel(stockLabel);
            }
            productCardResponseDTOS.add(productCardResponseDTO);

        }
        return productCardResponseDTOS;
    }

    public List<ProductCardResponseDTO> getProductsByCategory(Long categoryId) {
        List<ProductCardResponseDTO> productCardResponseDTOS = new ArrayList<>();
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Invalid Tenant");
        }
        Optional<Vendor> vendor = vendorRepository.findByTenantId(tenantId);
        if (vendor.isEmpty()) {
            throw new RuntimeException("Tenant Does not exist");
        }
        Vendor v1 = vendor.get();

        boolean existence = categoryRepository.existsByTenantIdAndIdAndStatus(tenantId, categoryId, CategoryStatus.ACTIVE);
        if (!existence) {
            throw new RuntimeException("Category does Not Exist...");
        }
        List<Product> products = productRepository.findAllByTenantIdAndStatusAndCategoryId(tenantId, ProductStatus.ACTIVE, categoryId);
        for (Product product : products) {
            ProductCardResponseDTO productCardResponseDTO = new ProductCardResponseDTO();
            productCardResponseDTO.setProductId(product.getId());
            productCardResponseDTO.setName(product.getProductName());
            productCardResponseDTO.setBusinessName(v1.getBussinessName());
            productCardResponseDTO.setImage(r2Properties.getPublicUrl() + "/" + product.getProductImage());
            productCardResponseDTO.setBrand(v1.getStoreName());
            productCardResponseDTO.setRating(4.4);
            productCardResponseDTO.setReviewCount(1200);
            productCardResponseDTO.setPrice(product.getSellingPrice());
            productCardResponseDTO.setOldPrice(product.getMrp());
            productCardResponseDTO.setDiscountPercent(getDiscountPrice(product.getSellingPrice(), product.getMrp()));
            productCardResponseDTO.setDeliveryEta("0-1 Days");
            String stockLabel = "";
            if (product.getStockQuantity() > 1) {
                stockLabel = "low_stock";
                productCardResponseDTO.setStockLevel(stockLabel);

            } else {
                stockLabel = "out_of_stock";
                productCardResponseDTO.setStockLevel(stockLabel);
            }
            productCardResponseDTOS.add(productCardResponseDTO);

        }
        return productCardResponseDTOS;

    }

    public List<ProductCardResponseDTO> findRecommendedProd() {
        List<ProductCardResponseDTO> productCardResponseDTOS = new ArrayList<>();
        String tenanId = TenantContext.getTenantId();
        if (tenanId == null) {
            throw new RuntimeException("Invalid Tenant");
        }
        Optional<Vendor> vendor = vendorRepository.findByTenantId(tenanId);
        if (vendor.isEmpty()) {
            throw new RuntimeException("Vendor Does Not existt");
        }
        Vendor v1 = vendor.get();
        List<Product> products = productRepository.findAllByTenantIdAndStatus(tenanId, ProductStatus.ACTIVE);
        for (Product product : products) {
            ProductCardResponseDTO productCardResponseDTO = new ProductCardResponseDTO();
            productCardResponseDTO.setProductId(product.getId());
            productCardResponseDTO.setName(product.getProductName());
            productCardResponseDTO.setBusinessName(v1.getBussinessName());
            productCardResponseDTO.setImage(r2Properties.getPublicUrl() + "/" + product.getProductImage());
            productCardResponseDTO.setBrand(v1.getStoreName());
            productCardResponseDTO.setRating(4.4);
            productCardResponseDTO.setReviewCount(1200);
            productCardResponseDTO.setVendorId(v1.getId());
            productCardResponseDTO.setPrice(product.getSellingPrice());
            productCardResponseDTO.setOldPrice(product.getMrp());
            productCardResponseDTO.setDiscountPercent(getDiscountPrice(product.getSellingPrice(), product.getMrp()));
            productCardResponseDTO.setDeliveryEta("0-1 Days");
            String stockLabel = "";
            if (product.getStockQuantity() > 1) {
                stockLabel = "low_stock";
                productCardResponseDTO.setStockLevel(stockLabel);

            } else {
                stockLabel = "out_of_stock";
                productCardResponseDTO.setStockLevel(stockLabel);
            }
            productCardResponseDTOS.add(productCardResponseDTO);

        }
        return productCardResponseDTOS;
    }

    public List<ProductCardResponseDTO> findNewArrivals() {
        List<ProductCardResponseDTO> productCardResponseDTOS = new ArrayList<>();
        String tenanId = TenantContext.getTenantId();
        if (tenanId == null) {
            throw new RuntimeException("Invalid Tenant");
        }
        Optional<Vendor> vendor = vendorRepository.findByTenantId(tenanId);
        if (vendor.isEmpty()) {
            throw new RuntimeException("Vendor Does Not exist");
        }
        Vendor v1 = vendor.get();
        List<Product> products =
                productRepository
                        .findTop10ByTenantIdAndStatusOrderByCreatedAtDesc(
                                tenanId,
                                ProductStatus.ACTIVE

                        );
        for (Product product : products) {
            ProductCardResponseDTO productCardResponseDTO = new ProductCardResponseDTO();
            productCardResponseDTO.setProductId(product.getId());
            productCardResponseDTO.setName(product.getProductName());
            productCardResponseDTO.setBusinessName(v1.getBussinessName());
            productCardResponseDTO.setImage(r2Properties.getPublicUrl() + "/" + product.getProductImage());
            productCardResponseDTO.setBrand(v1.getStoreName());
            productCardResponseDTO.setRating(4.4);
            productCardResponseDTO.setReviewCount(1200);
            productCardResponseDTO.setVendorId(v1.getId());
            productCardResponseDTO.setPrice(product.getSellingPrice());
            productCardResponseDTO.setOldPrice(product.getMrp());
            productCardResponseDTO.setDiscountPercent(getDiscountPrice(product.getSellingPrice(), product.getMrp()));
            productCardResponseDTO.setDeliveryEta("0-1 Days");
            String stockLabel = "";
            if (product.getStockQuantity() > 1) {
                stockLabel = "low_stock";
                productCardResponseDTO.setStockLevel(stockLabel);

            } else {
                stockLabel = "out_of_stock";
                productCardResponseDTO.setStockLevel(stockLabel);
            }
            productCardResponseDTOS.add(productCardResponseDTO);

        }
        return productCardResponseDTOS;
    }

    public List<ProductCardResponseDTO> findBestSeller() {
        List<ProductCardResponseDTO> productCardResponseDTOS = new ArrayList<>();
        String tenanId = TenantContext.getTenantId();
        if (tenanId == null) {
            throw new RuntimeException("Invalid Tenant");
        }
        Optional<Vendor> vendor = vendorRepository.findByTenantId(tenanId);
        if (vendor.isEmpty()) {
            throw new RuntimeException("Vendor Does Not exist");
        }
        Vendor v1 = vendor.get();
        List<Product> products =
                productRepository
                        .findTop10ByTenantIdAndStatusOrderByTotalSold(
                                tenanId,
                                ProductStatus.ACTIVE

                        );
        for (Product product : products) {
            ProductCardResponseDTO productCardResponseDTO = new ProductCardResponseDTO();
            productCardResponseDTO.setProductId(product.getId());
            productCardResponseDTO.setName(product.getProductName());
            productCardResponseDTO.setBusinessName(v1.getBussinessName());
            productCardResponseDTO.setImage(r2Properties.getPublicUrl() + "/" + product.getProductImage());
            productCardResponseDTO.setBrand(v1.getStoreName());
            productCardResponseDTO.setRating(4.4);
            productCardResponseDTO.setReviewCount(1200);
            productCardResponseDTO.setVendorId(v1.getId());
            productCardResponseDTO.setPrice(product.getSellingPrice());
            productCardResponseDTO.setOldPrice(product.getMrp());
            productCardResponseDTO.setDiscountPercent(getDiscountPrice(product.getSellingPrice(), product.getMrp()));
            productCardResponseDTO.setDeliveryEta("0-1 Days");
            String stockLabel = "";
            if (product.getStockQuantity() > 1) {
                stockLabel = "low_stock";
                productCardResponseDTO.setStockLevel(stockLabel);

            } else {
                stockLabel = "out_of_stock";
                productCardResponseDTO.setStockLevel(stockLabel);
            }
            productCardResponseDTOS.add(productCardResponseDTO);

        }
        return productCardResponseDTOS;
    }

    public String changeMyPassword(ChangePasswordDTO changePasswordDTO, CustomUserDetail userDetail) {
        String tenaantId = TenantContext.getTenantId();
        if (tenaantId == null) {
            throw new RuntimeException("Invalid tenent");
        }
        if (userDetail == null) {
            throw new RuntimeException("Please Login...");
        }
        User user = userDetail.getUser();
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new RuntimeException("New Password and Confirmm passWord should be same...");
        }
        if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current passwor is not matching");

        }
        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy(user.getFirstName());
        userRepos.save(user);
        return "passsword changes Successfully";
    }

    @Cacheable(value = "banners", key = "T(com.e_commerce.eCommerce.config.TenantContext).getTenantId()")
    public List<UserBannerResponseDTo> loadBanners() {
        String tenaantId = TenantContext.getTenantId();
        if (tenaantId == null) {
            throw new RuntimeException("Invalid tenent");
        }
        Optional<Vendor> vendor = vendorRepository.findByTenantId(tenaantId);
        if (vendor.isEmpty()) {
            throw new RuntimeException("Vendor Does Not exist");
        }
        Vendor v1 = vendor.get();

        List<UserBannerResponseDTo> userBannerResponseDTo = bannerRepository.findActiveBanners(tenaantId, v1.getId(), LocalDateTime.now());
        return userBannerResponseDTo;

    }

    @Transactional
    public void saveSubscribedEmail(String email) {
        log.error("Inside Subscribed Email");

        String tenantId = TenantContext.getTenantId();

        Optional<EmailSubscriber> existingSubscriber =
                newsletterSubscriberRepository
                        .findByTenantIdAndEmail(tenantId, email);

        if (existingSubscriber.isPresent()) {

            EmailSubscriber subscriber =
                    existingSubscriber.get();
            if (!subscriber.isSubscribed()) {
                subscriber.setSubscribed(true);
                subscriber.setUpdatedAt(LocalDateTime.now());
                newsletterSubscriberRepository.save(subscriber);
            }

            return;
        }

        EmailSubscriber subscriber =
                EmailSubscriber.builder()
                        .tenantId(tenantId)
                        .email(email)
                        .subscribed(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        newsletterSubscriberRepository.save(subscriber);
    }
}
