package com.e_commerce.eCommerce.entity;

import com.e_commerce.eCommerce.enums.PolicyStatus;
import com.e_commerce.eCommerce.enums.PolicyType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "vendor_policy",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "tenant_id",
                                "vendor_id",
                                "policy_type"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false, length = 100)
    private String tenantId;

    @Column(name = "vendor_id", nullable = false)
    private Long vendorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "policy_type", nullable = false)
    private PolicyType policyType;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    /**
     * Publish Status
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyStatus status;

    /**
     * Audit
     */
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}