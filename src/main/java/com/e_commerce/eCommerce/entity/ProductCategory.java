package com.e_commerce.eCommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "product_categories",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_tenant_category_name",
                        columnNames = {"tenant_id", "category_name"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "tenant_id", nullable = false, length = 100)
    private String tenantId;


    @Column(name = "vendor_id", nullable = false)
    private Long vendorId;

    /**
     * Category Name
     */
    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;

    /**
     * Category Description
     */
    @Column(length = 500)
    private String description;

    /**
     * Category Image
     */
    @Column(name = "image_url")
    private String imageUrl;

    /**
     * Category Status
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryStatus status;


    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}