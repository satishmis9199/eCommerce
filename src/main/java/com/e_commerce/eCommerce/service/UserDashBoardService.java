package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.config.R2Properties;
import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.*;
import com.e_commerce.eCommerce.dto.request.EmailRequestDto;
import com.e_commerce.eCommerce.entity.*;
import com.e_commerce.eCommerce.enums.PolicyStatus;
import com.e_commerce.eCommerce.enums.PolicyType;
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
import java.util.Map;
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
    private final EmailService emailService;
    private final VendorPolicyRepository vendorPolicyRepository;
    private final WiShlistRepositorye wiShlistRepositorye;

    public StoreInfoResponseDTO getStoreInfo() {

        String tenantId = TenantContext.getTenantId();

        if (tenantId == null || tenantId.isBlank()) {
            throw new RuntimeException("Tenant ID is missing");
        }

        Vendor vendor = vendorRepository.findByTenantId(tenantId)
                .orElseThrow(() ->
                        new RuntimeException("Vendor does not exist"));

        VendorBusiness vendorBusiness =
                vendorBussinesss.findByVendorId(vendor.getId());

        VendorBranding branding =
                vendorBrandingRepository.findByVendorId(vendor.getId());

        VendorAddress address =
                vendorAddresssRepo.findByVendorId(vendor.getId());

        StoreInfoResponseDTO dto = new StoreInfoResponseDTO();

        SocialMediaDTO socialMediaDTO = new SocialMediaDTO();

        StorePolicyDTO storePolicyDTO = new StorePolicyDTO();
        dto.setVendorId(vendor.getId());
        dto.setTenantId(vendor.getTenantId());
        dto.setBusinessName(vendor.getBussinessName());

        dto.setStoreName(vendor.getStoreName());

        if (vendorBusiness != null) {

            dto.setStoreType(
                    vendorBusiness.getBusinessCategory()
            );
        }



        if (branding != null) {

            dto.setTagline(
                    branding.getStoreTagline()
            );

            dto.setFaviconUrl(branding.getFaviconUrl());

            dto.setAboutUs(
                    branding.getStoreDescription()
            );

            socialMediaDTO.setFacebook(
                    branding.getFacebookUrl()
            );

            socialMediaDTO.setInstagram(
                    branding.getInstagramUrl()
            );

            socialMediaDTO.setLinkedin(
                    branding.getLinkedinUrl()
            );

            socialMediaDTO.setYoutube(
                    branding.getYoutubeUrl()
            );

            socialMediaDTO.setWhatsapp(
                    branding.getWhatsApp()
            );

            dto.setLogoUrl(
                    branding.getLogoUrl()
            );

            dto.setBannerUrl(
                    branding.getBannerUrl()
            );

            dto.setThemeColor(
                    branding.getPrimaryColor()
            );

            dto.setSupportEmail(
                    branding.getSupportEmail()
            );

            dto.setSupportPhone(
                    branding.getSupportPhone()
            );
        }

        if (address != null) {

            StringBuilder sb = new StringBuilder();

            if (address.getAddressLine1() != null
                    && !address.getAddressLine1().isBlank()) {

                sb.append(address.getAddressLine1());
            }

            if (address.getAddressLine2() != null
                    && !address.getAddressLine2().isBlank()) {

                if (sb.length() > 0) {
                    sb.append(", ");
                }

                sb.append(address.getAddressLine2());
            }

            if (address.getCity() != null
                    && !address.getCity().isBlank()) {

                if (sb.length() > 0) {
                    sb.append(", ");
                }

                sb.append(address.getCity());
            }

            if (address.getState() != null
                    && !address.getState().isBlank()) {

                if (sb.length() > 0) {
                    sb.append(", ");
                }

                sb.append(address.getState());
            }

            if (address.getCountry() != null
                    && !address.getCountry().isBlank()) {

                if (sb.length() > 0) {
                    sb.append(", ");
                }

                sb.append(address.getCountry());
            }

            if (address.getPostalCode() != null
                    && !address.getPostalCode().isBlank()) {

                if (sb.length() > 0) {
                    sb.append(" - ");
                }

                sb.append(address.getPostalCode());
            }

            dto.setAddress(sb.toString());
        }

        dto.setSocialMedia(socialMediaDTO);

        List<VendorPolicy> policies =
                vendorPolicyRepository
                        .findByTenantIdAndVendorIdAndStatus(
                                tenantId,
                                vendor.getId(),
                                PolicyStatus.PUBLISHED
                        );


        for (VendorPolicy policy : policies) {

            if (policy.getPolicyType() == PolicyType.ABOUT_US) {

                dto.setAboutUs(
                        policy.getContent()
                );

            } else if (policy.getPolicyType() == PolicyType.CONTACT_US) {
                continue;
            } else if (policy.getPolicyType() == PolicyType.PRIVACY_POLICY) {

                storePolicyDTO.setPrivacyPolicy(
                        policy.getContent()
                );

            } else if (policy.getPolicyType() == PolicyType.TERMS_AND_CONDITIONS) {

                storePolicyDTO.setTermsAndConditions(
                        policy.getContent()
                );

            } else if (policy.getPolicyType() == PolicyType.RETURN_POLICY) {

                storePolicyDTO.setReturnPolicy(
                        policy.getContent()
                );

            } else if (policy.getPolicyType() == PolicyType.REFUND_POLICY) {
                continue;
            } else if (policy.getPolicyType() == PolicyType.SHIPPING_POLICY) {

                storePolicyDTO.setShippingPolicy(
                        policy.getContent()
                );

            } else if (policy.getPolicyType() == PolicyType.CANCELLATION_POLICY) {

                storePolicyDTO.setCancellationPolicy(
                        policy.getContent()
                );

            } else if (policy.getPolicyType() == PolicyType.WARRANTY_POLICY) {


            } else if (policy.getPolicyType() == PolicyType.EXCHANGE_POLICY) {

            }
        }
        dto.setPolicies(storePolicyDTO);
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
        String tenantId = TenantContext.getTenantId();
        Optional<Vendor> vendor=vendorRepository.findByTenantId(tenantId);
        if(vendor.isEmpty()){
            throw new RuntimeException("Vendor Does Not Exist");
        }
      Vendor v1=vendor.get();
        Optional<EmailSubscriber> existingSubscriber =
                newsletterSubscriberRepository
                        .findByTenantIdAndEmail(tenantId, email);

        if (existingSubscriber.isPresent()) {
            log.error("Already Present email");

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
        log.error("Email Sibscribed");

        newsletterSubscriberRepository.save(subscriber);
        String storeUrl="https://"+v1.getSubDomain();


        EmailRequestDto emailRequest = EmailRequestDto.builder()
                .to(email)
                .subject("Welcome to Our Newsletter — Enjoy Your Exclusive Offer")
                .templateName("subscribed-welcome")
                .templateVariables(Map.of(
                        "tenantName",v1.getStoreName() ,
                        "email", v1.getEmail(),
                        "storeUrl",storeUrl

                ))
                .build();
        emailService.sendEmailAsync(emailRequest
        );

    }


    public void addToWishlist(Long productId, CustomUserDetail userDetail) {

        Long userId = userDetail.getId();
        String tenantId = TenantContext.getTenantId();

        User user = userRepos.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (wiShlistRepositorye.existsByUserIdAndProductId(userId, productId)) {
            throw new RuntimeException("Product already in wishlist");
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProduct(product);
        wishlist.setTenantId(tenantId);

        wiShlistRepositorye.save(wishlist);
    }
}
