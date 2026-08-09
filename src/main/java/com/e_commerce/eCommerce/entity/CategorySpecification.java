package com.e_commerce.eCommerce.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "category_specification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategorySpecification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false, length = 100)
    private String tenantId;

    @Column(name = "vendor_id", nullable = false)
    private Long vendorId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "specification_name", nullable = false, length = 150)
    private String specificationName;

    @Column(name = "is_required", nullable = false)
    private boolean required;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryStatus status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = CategoryStatus.ACTIVE;
        }
        if (displayOrder == null) {
            displayOrder = 1;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}