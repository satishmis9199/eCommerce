package com.e_commerce.eCommerce.entity;

import com.e_commerce.eCommerce.dto.BannerLinkType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * NOTE: mirrors the ProductCategory entity pattern already in your codebase
 * (tenantId + vendorId set server-side from the authenticated session,
 * never accepted from the client). If you have a shared BaseTenantEntity /
 * BaseVendorEntity superclass, extend that instead of repeating the
 * id/tenantId/vendorId/createdAt/updatedAt fields here.
 */
@Entity
@Table(name = "banners", indexes = {
        @Index(name = "idx_banner_tenant_vendor_active_order", columnList = "tenantId, vendorId, active, displayOrder")
})
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Banner {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private String tenantId;

    @Column(nullable = false, updatable = false)
    private Long vendorId;

    @Column(length = 100)
    private String eyebrow;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 250)
    private String subtitle;

    @Column(length = 60)
    private String ctaLabel;
    @Column(length = 500)
    private String ctaLink;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private BannerLinkType ctaLinkType;
    @Column(nullable = false, length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private Integer displayOrder = 0;

    @Column(nullable = false)
    private Boolean active = Boolean.TRUE;
    private LocalDateTime startsAt;
    @Column(nullable = true)
    private LocalDateTime endsAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}