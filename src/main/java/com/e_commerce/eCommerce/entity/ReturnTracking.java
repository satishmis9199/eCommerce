package com.e_commerce.eCommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "return_tracking")
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReturnTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Multi Tenant
    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;

    // Vendor
    @Column(name = "vendor_id", nullable = false)
    private Long vendorId;

    // Order
    @Column(name = "order_id", nullable = false)
    private Long orderId;


    @Column(name = "previous_status", length = 40)
    private String previousStatus;

    // Current Return Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private ReturnStatus status;

    // Message shown to customer
    @Column(name = "remarks", length = 250)
    private String remarks;

    // Optional location
    @Column(name = "location", length = 150)
    private String location;

    // Who changed status
    @Column(name = "changed_by")
    private Long changedBy;

    // CUSTOMER / ADMIN / SYSTEM
    @Column(name = "changed_by_type", length = 20)
    private String changedByType;

    @Builder.Default
    @Column(name = "row_state", nullable = false)
    private Integer rowState = 1;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}