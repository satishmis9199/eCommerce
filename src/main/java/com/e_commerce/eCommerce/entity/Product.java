package com.e_commerce.eCommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Multi Tenant
     */
    @Column(nullable = false, length = 100)
    private String tenantId;

    @Column(nullable = false)
    private Long vendorId;

    @Column(nullable = false)
    private Long categoryId;

    /**
     * Product Information
     */
    @Column(nullable = false, length = 200)
    private String productName;

    @Lob
    private String description;

    /**
     * Pricing
     */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal sellingPrice;

    @Column(precision = 12, scale = 2)
    private BigDecimal mrp;

    @Builder.Default
    @Column(nullable = false)
    private Boolean featured = false;

    /**
     * Inventory
     */
    @Column(nullable = false)
    private Integer stockQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductUnit unit;

    @Column
    private String productImage;

    @Builder.Default
    @Column(nullable = false)
    private Long totalSold = 0L;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @Column(nullable = false)
    private Long createdBy;

    @Column(nullable = false)
    private Long updatedBy;

    /**
     * Audit
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;

        if (this.totalSold == null) {
            this.totalSold = 0L;
        }

        if (this.featured == null) {
            this.featured = false;
        }

        if (this.updatedBy == null) {
            this.updatedBy = 0L;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();


        if (this.updatedBy == null) {
            this.updatedBy = 0L;
        }
        if (this.createdBy == null) {
            this.createdBy = 0L;
        }
    }
}