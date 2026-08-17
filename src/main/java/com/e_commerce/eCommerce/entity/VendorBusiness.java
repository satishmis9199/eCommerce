package com.e_commerce.eCommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "vendor_business",
        indexes = {
                @Index(name = "idx_business_vendor", columnList = "vendor_id"),
                @Index(name = "idx_business_gst", columnList = "gstNumber"),
                @Index(name = "idx_business_pan", columnList = "panNumber")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorBusiness {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "vendor_id",
            nullable = false,
            unique = true
    )
    private Vendor vendor;

    private String tenant_Id;
    @Column(nullable = false)
    private BusinessType businessType;

    @Column(nullable = false)
    private String businessCategory;

    @Column(unique = true)
    private String gstNumber;

    @Column(unique = true)
    private String panNumber;

    @Column(length = 3000)
    private String businessDescription;

    private String website;

    private String cinNumber;

    private String msmeNumber;

    private String fssaiNumber;

    private Integer establishedYear;

    private Integer employeeCount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        businessType = BusinessType.PRIVATE_LIMITED;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

}