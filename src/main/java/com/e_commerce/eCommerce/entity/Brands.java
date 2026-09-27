package com.e_commerce.eCommerce.entity;

import com.e_commerce.eCommerce.enums.BrandStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "brands",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_brand_tenant_name",
                        columnNames = {"tenant_id", "brand_name"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_brand_tenant",
                        columnList = "tenant_id"
                ),
                @Index(
                        name = "idx_brand_vendor",
                        columnList = "vendor_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brands {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "brand_name",
            nullable = false,
            length = 150
    )
    private String brandName;

    @Column(
            name = "brand_desc",
            columnDefinition = "TEXT"
    )
    private String brandDesc;

    @Column(
            name = "tenant_id",
            nullable = false,
            length = 100
    )
    private String tenantId;

    @Column(
            name = "vendor_id",
            nullable = false
    )
    private Long vendorId;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @Column(
            name = "created_by",
            nullable = false
    )
    private Long createdBy;

    @Column(
            name = "updated_by",
            nullable = false
    )
    private Long updatedBy;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BrandStatus brandStatus = BrandStatus.ACTIVE;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}