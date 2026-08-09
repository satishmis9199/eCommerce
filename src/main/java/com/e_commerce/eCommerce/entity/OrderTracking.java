package com.e_commerce.eCommerce.entity;

import com.e_commerce.eCommerce.entity.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "order_tracking",
        indexes = {
                @Index(name = "idx_tracking_order", columnList = "order_id"),
                @Index(name = "idx_tracking_status", columnList = "status"),
                @Index(name = "idx_tracking_tenant", columnList = "tenant_id"),
                @Index(name = "idx_tracking_vendor", columnList = "vendor_id"),
                @Index(name = "idx_tracking_created", columnList = "created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderTracking {

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
    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 40)
    private OrderStatus previousStatus;

    // Current Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private OrderStatus status;

    // Status message shown to customer
    @Column(length = 250)
    private String remarks;

    // Optional Location
    @Column(length = 150)
    private String location;



    // Who changed the status
    @Column(name = "changed_by")
    private Long changedBy;

    // Admin / Vendor / System
    @Column(name = "changed_by_type", length = 20)
    private String changedByType;

    @Builder.Default
    @Column(name = "row_state")
    private Integer rowState = 1;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

}