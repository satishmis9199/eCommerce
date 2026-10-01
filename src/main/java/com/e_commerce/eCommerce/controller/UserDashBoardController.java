package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.CustomAnnotation.AuditLogs;
import com.e_commerce.eCommerce.CustomAnnotation.RequiresFeature;
import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.*;
import com.e_commerce.eCommerce.dto.request.ProductFilterDTO;
import com.e_commerce.eCommerce.dto.response.BrandResponseDTO;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import com.e_commerce.eCommerce.service.UserDashBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/u1/v1")
@Slf4j
@RequiredArgsConstructor
public class UserDashBoardController {

    private final UserDashBoardService userDashBoardService;
    @AuditLogs("StoreInfo")



    @GetMapping("/brand-details")
    public ResponseEntity<ApiResponse<?>> getActiveBrand(){
        String tenantId=TenantContext.getTenantId();
        List<BrandResponseDTO> brandResponseDTOS=userDashBoardService.getActiveBrand(tenantId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Active Brand",
                                brandResponseDTOS
                        )
                );
    }
    @GetMapping("/store/info")
    public StoreInfoResponseDTO getStoreInfo() {
        return userDashBoardService.getStoreInfo();


    }
    @AuditLogs("getActiveCategory")
    @RequiresFeature("CATEGORY_MANAGEMENT")
    @GetMapping("/catalog/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> getActiveCategory() {

        List<CategoryResponseDTO> categories = userDashBoardService.getActiveCategory();

        if (!categories.isEmpty()) {
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Categories fetched successfully",
                            categories
                    )
            );
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        new ApiResponse<>(
                                false,
                                "No categories found",
                                null
                        )
                );
    }
    @AuditLogs("getFeaturedProducts")
    @GetMapping("/products/featured")
    public ResponseEntity<ApiResponse<List<ProductCardResponseDTO>>> getFeaturedProduct(
            @RequestParam(required = false) List<Long> categoryId,
            @RequestParam(required = false) List<String> brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer rating){


            ProductFilterDTO filterDTO=new ProductFilterDTO(categoryId,brand,minPrice,maxPrice,rating);
            String tenant = TenantContext.getTenantId();
            List<ProductCardResponseDTO> productCardResponseDTOS = userDashBoardService.getFeaturedProd(filterDTO,tenant);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse<>(
                                    true,
                                    "Featured Product Fetched Successfully",
                                    productCardResponseDTOS
                            )
                    );

    }
    @AuditLogs("GetAllProduct")
    @GetMapping("/products")
    @RequiresFeature("PRODUCT_MANAGEMENT")
    public ResponseEntity<ApiResponse<List<ProductCardResponseDTO>>> getAllProducts(
            @RequestParam(required = false) List<Long> categoryId,
            @RequestParam(required = false) List<String> brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer rating) {

            String tenant = TenantContext.getTenantId();
            ProductFilterDTO filter = new ProductFilterDTO(categoryId, brand, minPrice, maxPrice, rating);
            List<ProductCardResponseDTO> productCardResponseDTOS =
                    userDashBoardService.getAllProducts(tenant, filter);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Product Fetched Successfully", productCardResponseDTOS));

    }
    @AuditLogs("getProductsByCategory")
    @RequiresFeature("CATEGORY_MANAGEMENT")
    @GetMapping("/products/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductCardResponseDTO>>> getProductsByCategory(@PathVariable Long categoryId) {

            List<ProductCardResponseDTO> productCardResponseDTOS = userDashBoardService.getProductsByCategory(categoryId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            true,
                            "Product fetched Successfully",
                            productCardResponseDTOS

                    ));


    }
    @AuditLogs("Recommended products")
    @RequiresFeature("PRODUCT_MANAGEMENT")
    @GetMapping("/products/recommended")
    public ResponseEntity<ApiResponse<List<ProductCardResponseDTO>>> getRecommendedProducts() {

            List<ProductCardResponseDTO> data = userDashBoardService.findRecommendedProd();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse<>(
                                    true,
                                    "Recommended Products",
                                    data
                            )
                    );


    }
    @AuditLogs("New Arrivals")
    @RequiresFeature("PRODUCT_MANAGEMENT")
    @GetMapping("/products/new-arrivals")
    public ResponseEntity<ApiResponse<List<ProductCardResponseDTO>>> getNewArrivals() {

            List<ProductCardResponseDTO> data = userDashBoardService.findNewArrivals();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse<>(
                                    true,
                                    "New Arrivals Recomended",
                                    data
                            )
                    );



    }
    @AuditLogs("Best Seller")
    @RequiresFeature("PRODUCT_MANAGEMENT")
    @GetMapping("/products/best-sellers")
    public ResponseEntity<ApiResponse<List<ProductCardResponseDTO>>> getBestSellProducts() {

            List<ProductCardResponseDTO> data = userDashBoardService.findBestSeller();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse<>(
                                    true,
                                    "Best Sell Items",
                                    data
                            )
                    );



    }

    @PostMapping("/users/me/change-password")
    public ResponseEntity<ApiResponse<?>> changePassword(
            @RequestBody ChangePasswordDTO changePasswordDTO,
            @AuthenticationPrincipal CustomUserDetail userDetail) {

            String message = userDashBoardService.changeMyPassword(changePasswordDTO, userDetail);

            log.info("Password changed successfully for user {}", userDetail.getId());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, message));


    }
    @AuditLogs("Loaded Festival banner")
    @RequiresFeature("FESTIVAL_BANNER")
    @GetMapping("/users/home/banners")
    public ResponseEntity<ApiResponse<List<UserBannerResponseDTo>>> loadBanner(
            @AuthenticationPrincipal CustomUserDetail userDetail) {



            List<UserBannerResponseDTo> banners =
                    userDashBoardService.loadBanners();

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Banners loaded successfully.",
                            banners
                    )
            );


    }
    @AuditLogs("Saved Subscribed Email")
    @PostMapping("/marketing/newsletter/subscribe")
    public ResponseEntity<ApiResponse<?>> saveSubscribedEmail(
            @RequestBody Map<String, String> request) {

            String email = request.get("email");

            if (email == null || email.trim().isEmpty()) {

                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(
                                false,
                                "Email is required."
                        ));
            }

            email = email.trim().toLowerCase();
           log.error("Email Subscribed Request Recieve  -- "+email);
            userDashBoardService.saveSubscribedEmail(email);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Successfully subscribed to newsletter."
                    )
            );


    }
}
