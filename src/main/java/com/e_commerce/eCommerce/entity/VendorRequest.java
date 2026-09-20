package com.e_commerce.eCommerce.entity;

import com.e_commerce.eCommerce.enums.VendorRequestStatus;
import jakarta.persistence.*; // use javax.persistence.* if you are on Spring Boot 2.x
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "vendor_requests",
        indexes = {
                @Index(name = "idx_vr_status_created", columnList = "status, created_at"),
                @Index(name = "idx_vr_email", columnList = "email"),
                @Index(name = "idx_vr_phone", columnList = "phone"),
                @Index(name = "idx_vr_ip_created", columnList = "ip_address, created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class VendorRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_name", nullable = false, length = 120)
    private String businessName;

    @Column(name = "owner_name", nullable = false, length = 100)
    private String ownerName;
    @Column(name = "request_code",  unique = true, length = 20)
    private String requestCode;

    @Column(nullable = false, length = 10)
    private String phone;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 80)
    private String city;

    @Column(nullable = false, length = 80)
    private String state;

    @Column(name = "business_category", nullable = false, length = 60)
    private String businessCategory;

    @Column(length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VendorRequestStatus status = VendorRequestStatus.PENDING;

    @Column(name = "admin_remarks", length = 500)
    private String adminRemarks;

    @Column(name = "reviewed_by", length = 120)
    private String reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}