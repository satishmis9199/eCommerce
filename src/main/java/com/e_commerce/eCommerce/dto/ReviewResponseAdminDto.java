package com.e_commerce.eCommerce.dto;

import com.e_commerce.eCommerce.entity.ReviewStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseAdminDto {

    /**
     * Review Information
     */
    private Long id;
    private ReviewStatus status;
    private Integer rating;
    private String reviewTitle;
    private String reviewText;
    private LocalDateTime createdAt;

    /**
     * Product Information
     */
    private Long productId;
    private String productName;
    private String productImage;

    /**
     * Customer Information
     */
    private Long userId;
    private String customerName;
    private String customerEmail;

    /**
     * Moderation Information
     */
    private Integer helpfulCount;
    private Integer reportCount;

    public ReviewResponseAdminDto(
            Long id,
            Long productId,
            String productName,
            String productImage,
            Long userId,
            String customerName,
            String customerEmail,
            Integer rating,
            String reviewTitle,
            String reviewText,
            ReviewStatus status,
            Integer helpfulCount,
            Integer reportCount,
            LocalDateTime createdAt) {

        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.userId = userId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.rating = rating;
        this.reviewTitle = reviewTitle;
        this.reviewText = reviewText;
        this.status = status;
        this.helpfulCount = helpfulCount;
        this.reportCount = reportCount;
        this.createdAt = createdAt;
    }
}