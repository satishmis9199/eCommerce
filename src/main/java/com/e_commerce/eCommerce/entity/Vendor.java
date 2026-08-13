package com.e_commerce.eCommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "vendors",
        indexes = {

                @Index(name = "idx_vendor_tenant", columnList = "tenantId"),

                @Index(name = "idx_vendor_email", columnList = "email"),

                @Index(name = "idx_vendor_mobile", columnList = "mobile"),

                @Index(name = "idx_vendor_status", columnList = "status"),

                @Index(name = "idx_vendor_subdomain", columnList = "subDomain")

        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, updatable = false, length = 36)
    private String tenantId;
    @Column(nullable = false)
    private String bussinessName;
    @Column
    private String invoiceNotes;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique=true)
    private String vendorEmail;

    public String getVendorEmail() {
        return vendorEmail;
    }

    public void setVendorEmail(String vendorEmail) {
        this.vendorEmail = vendorEmail;
    }

    @Column(nullable = false, unique = true)
    private String mobile;

    @Column(nullable = false)
    private String password;




    @Column(nullable = false)
    private String storeName;



    @Column(nullable = false, unique = true)
    private String subDomain;

    private SubscriptionPlan plan;

    private String logo;
    @Column(nullable = false)
    private boolean reSubmit;

    private boolean customDomainVerified;



    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VendorStatus status;

    @Column(nullable = false)
    private boolean completeVerified;


    private String role;

    private boolean active;

    private boolean emailVerified;

    private boolean mobileVerified;

    private boolean deleted;

    private Long createdBy;

    private Long updatedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    @PrePersist
    public void prePersist() {
        reSubmit=false;

        tenantId = UUID.randomUUID().toString();

        createdAt = LocalDateTime.now();


        updatedAt = LocalDateTime.now();

        active = false;

        deleted = false;

        emailVerified = false;

        mobileVerified = false;

        completeVerified=false;

        role = "VENDOR";

        status = VendorStatus.ONBOARDING;

    }


    @PreUpdate
    public void preUpdate() {

        updatedAt = LocalDateTime.now();

    }

}