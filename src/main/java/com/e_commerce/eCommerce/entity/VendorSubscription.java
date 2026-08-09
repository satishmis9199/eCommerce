package com.e_commerce.eCommerce.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendor_subscription")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @Column(nullable = false, length = 36)
    private String tenantId;



    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "vendor_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_vendor_subscription")
    )
    private Vendor vendor;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionPlan subscriptionPlan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus subscriptionStatus;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate expiryDate;





    // =====================================
    // Renewal
    // =====================================

    private Boolean autoRenew;

    private LocalDate nextRenewalDate;

    // =====================================
    // Audit
    // =====================================

    private Long createdBy;

    private Long updatedBy;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // =====================================
    // Entity Lifecycle
    // =====================================

    @PrePersist
    public void prePersist() {

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (subscriptionStatus == null) {
            subscriptionStatus = SubscriptionStatus.ACTIVE;
        }




        if (autoRenew == null)
            autoRenew = false;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

}