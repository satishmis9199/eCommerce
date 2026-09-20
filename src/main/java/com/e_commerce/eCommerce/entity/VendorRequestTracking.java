package com.e_commerce.eCommerce.entity;

import com.e_commerce.eCommerce.enums.VendorRequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "vendor_request_tracking",
        indexes = {
                @Index(
                        name = "idx_vrt_request_created",
                        columnList = "vendor_request_id, created_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorRequestTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "vendor_request_id",
            nullable = false
    )
    private VendorRequest vendorRequest;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VendorRequestStatus status;

    @Column(length = 500)
    private String message;

    @Column(length = 500)
    private String remarks;

    @Column(name = "changed_by", length = 120)
    private String changedBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}