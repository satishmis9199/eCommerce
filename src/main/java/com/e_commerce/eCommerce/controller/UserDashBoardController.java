package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.*;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import com.e_commerce.eCommerce.service.UserDashBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/u1/v1")
@Slf4j
@RequiredArgsConstructor
public class UserDashBoardController {

    private final UserDashBoardService userDashBoardService;

    @GetMapping("/store/info")
    public StoreInfoResponseDTO getStoreInfo() {
        return userDashBoardService.getStoreInfo();


    }


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

    @GetMapping("/products/featured")
    public ResponseEntity<ApiResponse<List<ProductCardResponseDTO>>> getFeaturedProduct() {
        try {
            String tenant = TenantContext.getTenantId();
            List<ProductCardResponseDTO> productCardResponseDTOS = userDashBoardService.getFeaturedProd(tenant);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse<>(
                                    true,
                                    "Featured Product Fetched Successfully",
                                    productCardResponseDTOS
                            )
                    );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    "Error While Featured Product Fetching",
                                    null
                            )
                    );
        }
    }

    @GetMapping("/products")

    public ResponseEntity<ApiResponse<List<ProductCardResponseDTO>>> getAllProducts() {
        try {
            String tenant = TenantContext.getTenantId();
            List<ProductCardResponseDTO> productCardResponseDTOS = userDashBoardService.getAllProducts(tenant);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse<>(
                                    true,
                                    " Product Fetched Successfully",
                                    productCardResponseDTOS
                            )
                    );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    "Error While Featured Product Fetching",
                                    null
                            )
                    );
        }
    }

    @GetMapping("/products/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductCardResponseDTO>>> getProductsByCategory(@PathVariable Long categoryId) {
        try {
            List<ProductCardResponseDTO> productCardResponseDTOS = userDashBoardService.getProductsByCategory(categoryId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            true,
                            "Product fetched Successfully",
                            productCardResponseDTOS

                    ));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    e.getMessage(),
                                    null
                            )
                    );
        }
    }

    @GetMapping("/products/recommended")
    public ResponseEntity<ApiResponse<List<ProductCardResponseDTO>>> getRecommendedProducts() {
        try {
            List<ProductCardResponseDTO> data = userDashBoardService.findRecommendedProd();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse<>(
                                    true,
                                    "Recommended Products",
                                    data
                            )
                    );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    e.getMessage(),
                                    null
                            )
                    );
        }
    }


    @GetMapping("/products/new-arrivals")
    public ResponseEntity<ApiResponse<List<ProductCardResponseDTO>>> getNewArrivals() {
        try {
            List<ProductCardResponseDTO> data = userDashBoardService.findNewArrivals();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse<>(
                                    true,
                                    "New Arrivals Recomended",
                                    data
                            )
                    );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    e.getMessage(),
                                    null
                            )
                    );
        }

    }

    @GetMapping("/products/best-sellers")
    public ResponseEntity<ApiResponse<List<ProductCardResponseDTO>>> getBestSellProducts() {
        try {
            List<ProductCardResponseDTO> data = userDashBoardService.findBestSeller();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse<>(
                                    true,
                                    "Best Sell Items",
                                    data
                            )
                    );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    e.getMessage(),
                                    null
                            )
                    );
        }

    }

    @PostMapping("/users/me/change-password")
    public ResponseEntity<ApiResponse<?>> changePassword(
            @RequestBody ChangePasswordDTO changePasswordDTO,
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        log.info("========== CHANGE PASSWORD REQUEST ==========");
        log.info("User Id : {}", userDetail != null ? userDetail.getId() : "NULL");
        log.info("Current Password : {}", changePasswordDTO.getCurrentPassword());
        log.info("New Password : {}", changePasswordDTO.getNewPassword());
        log.info("Confirm Password : {}", changePasswordDTO.getConfirmPassword());

        try {

            String message = userDashBoardService.changeMyPassword(changePasswordDTO, userDetail);

            log.info("Password changed successfully for user {}", userDetail.getId());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, message));

        } catch (Exception e) {

            log.error("========== CHANGE PASSWORD ERROR ==========");
            log.error("Exception Type : {}", e.getClass().getName());
            log.error("Exception Message : {}", e.getMessage());
            log.error("Stack Trace :", e);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            false,
                            e.getMessage() != null ? e.getMessage() : "Something went wrong."
                    ));
        }
    }

    @GetMapping("/users/home/banners")
    public ResponseEntity<ApiResponse<List<UserBannerResponseDTo>>> loadBanner(
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        try {

            List<UserBannerResponseDTo> banners =
                    userDashBoardService.loadBanners();

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Banners loaded successfully.",
                            banners
                    )
            );

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(
                            false,
                            e.getMessage()
                    ));
        }
    }

}
