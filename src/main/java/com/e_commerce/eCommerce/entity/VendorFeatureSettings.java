package com.e_commerce.eCommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "vendor_feature_settings",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_vendor_feature_settings",
                        columnNames = {"tenant_id", "vendor_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorFeatureSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "vendor_id", nullable = false)
    private Long vendorId;

    @Column(name = "online_payment_enabled", nullable = false)
    @Builder.Default
    private boolean onlinePaymentEnabled = true;

    @Column(name = "cod_enabled", nullable = false)
    @Builder.Default
    private boolean codEnabled = true;

    @Column(name = "wishlist_enabled", nullable = false)
    @Builder.Default
    private boolean wishlistEnabled = true;

    @Column(name = "reviews_enabled", nullable = false)
    @Builder.Default
    private boolean reviewsEnabled = true;

    @Column(name = "chat_enabled", nullable = false)
    @Builder.Default
    private boolean chatEnabled = true;

    @Column(name = "coupons_enabled", nullable = false)
    @Builder.Default
    private boolean couponsEnabled = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}