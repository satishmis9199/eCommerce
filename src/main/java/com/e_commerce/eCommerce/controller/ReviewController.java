package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.dto.ApiResponse;
import com.e_commerce.eCommerce.dto.ReviewResponseAdminDto;
import com.e_commerce.eCommerce.dto.UserReviewResponseDTO;
import com.e_commerce.eCommerce.entity.ReviewStatus;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import com.e_commerce.eCommerce.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping(value = "/vendor/s2/v1/reviews",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Page<ReviewResponseAdminDto>>> getReviewWithStatus(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @RequestParam(required = false) ReviewStatus status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        try {

            Pageable pageable = PageRequest.of(page, size);

            Page<ReviewResponseAdminDto> reviews =
                    reviewService.getAllReviewForAdmin(
                            userDetail,
                            status,
                            pageable
                    );

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Reviews fetched successfully.",
                            reviews
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
    @PatchMapping(value = "/vendor/s2/v1/reviews/{id}/status",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<?>> updateReviewStatus(@PathVariable Long id, @RequestBody Map<String,ReviewStatus> action,@AuthenticationPrincipal CustomUserDetail userDetail){
        try {


            String reviews =
                    reviewService.updateReviewStatus(action,userDetail,id);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Reviews fetched successfully.",
                            reviews
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
    @GetMapping("/api/u1/v1/store/reviews")
    public ResponseEntity<ApiResponse<List<UserReviewResponseDTO>>> getVerifiedReviwed(){
        try {


           List<UserReviewResponseDTO> userReviewResponseDTOS=reviewService.findVerifiedReviewed();

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Reviews fetched successfully.",
                            userReviewResponseDTOS
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
}