package com.e_commerce.eCommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vendor_onboarding_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorOnboardingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tenantId;

    @Column(name = "vendor_id")
    private Long vendorId;

    @Enumerated(EnumType.STRING)
    private OnboardingStatus fromStatus;

    private String remarks;

    @Enumerated(EnumType.STRING)
    private OnboardingStatus toStatus;

    private LocalDateTime createdAt;
    private String updatedBy;

    private int rowtate = 1;
}