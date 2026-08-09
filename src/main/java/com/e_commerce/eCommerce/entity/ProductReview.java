package com.e_commerce.eCommerce.entity;

import com.e_commerce.eCommerce.entity.ReviewStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "product_review",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_order_item_review",
                        columnNames = "order_item_id"
                )
        },
        indexes = {
                @Index(name = "idx_review_product", columnList = "product_id"),
                @Index(name = "idx_review_user", columnList = "user_id"),
                @Index(name = "idx_review_vendor", columnList = "vendor_id"),
                @Index(name = "idx_review_tenant", columnList = "tenant_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;

    @Column(name = "vendor_id", nullable = false)
    private Long vendorId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "order_item_id", nullable = false)
    private Long orderItemId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer rating;

    @Column(name = "review_title", length = 150)
    private String reviewTitle;

    @Column(name = "review_text", length = 2000)
    private String reviewText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReviewStatus status;

    @Builder.Default
    @Column(name = "helpful_count")
    private Integer helpfulCount = 0;

    @Builder.Default
    @Column(name = "report_count")
    private Integer reportCount = 0;

    @Builder.Default
    @Column(name = "row_state")
    private Integer rowState = 1;

    @Column(name = "created_by")
    private Long createdBy;


    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (status == null) {
            status = ReviewStatus.PENDING;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}