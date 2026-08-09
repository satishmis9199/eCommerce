package com.e_commerce.eCommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "vendor_branding",
        indexes = {
                @Index(name = "idx_vendor_branding_vendor", columnList = "vendor_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorBranding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //========================================
    // Vendor
    //========================================

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "vendor_id",
            nullable = false,
            unique = true
    )
    private Vendor vendor;

    //========================================
    // Branding
    //========================================

    @Column(length = 1000)
    private String logoUrl;

    @Column(length = 1000)
    private String bannerUrl;

    @Column(length = 1000)
    private String faviconUrl;

    @Column(length = 20)
    private String primaryColor;

    @Column(length = 20)
    private String secondaryColor;

    @Column(length = 200)
    private String storeTagline;

    @Column(length = 2000)
    private String storeDescription;

    @Column(length = 1000)
    private String supportEmail;

    @Column(length = 1000)
    private String supportPhone;

    //========================================
    // SEO
    //========================================

    @Column(length = 200)
    private String metaTitle;

    @Column(length = 500)
    private String metaDescription;

    @Column(length = 1000)
    private String metaKeywords;

    //========================================
    // Audit
    //========================================

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}