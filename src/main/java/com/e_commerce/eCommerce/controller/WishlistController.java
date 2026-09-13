
        package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.dto.ApiResponse;
import com.e_commerce.eCommerce.dto.response.WishlistResponseDto;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import com.e_commerce.eCommerce.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/u1/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {
    private final WishlistService wishlistService;
    @PostMapping("/{productId}")
    public ResponseEntity<ApiResponse<WishlistResponseDto>> addToWishlist(
            @PathVariable Long productId,
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        WishlistResponseDto data =
                wishlistService.addToWishlist(productId, userDetail);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        "Product added to wishlist successfully",
                        data
                ));
    }
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(
            @PathVariable Long productId,
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        wishlistService.removeFromWishlist(productId, userDetail);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Product removed from wishlist successfully",
                        null
                )
        );
    }


    @GetMapping
    public ResponseEntity<ApiResponse<List<WishlistResponseDto>>> getWishlist(
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        List<WishlistResponseDto> data =
                wishlistService.getWishlist(userDetail);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Wishlist loaded successfully",
                        data
                )
        );
    }


    @GetMapping("/check/{productId}")
    public ResponseEntity<ApiResponse<Boolean>> checkWishlist(
            @PathVariable Long productId,
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        boolean wishlisted =
                wishlistService.isWishlisted(productId, userDetail);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        wishlisted,
                        "Wishlist status fetched successfully",
                        true
                )
        );
    }
}
