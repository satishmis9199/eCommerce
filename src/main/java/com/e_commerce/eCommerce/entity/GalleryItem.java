package com.e_commerce.eCommerce.entity;

import com.e_commerce.eCommerce.enums.MediaType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "gallery_item",
        indexes = @Index(name = "idx_gallery_tenant_active", columnList = "tenant_id, active, deleted, sort_order"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GalleryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false, length = 64)
    private String tenantId;

    @Column(name = "vendor_id")
    private Long vendorId;               // kisne add kiya (sirf record ke liye)

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false, length = 10)
    private MediaType mediaType;

    @Column(name = "media_url", nullable = false, length = 1000)
    private String mediaUrl;             // R2 key ya https link

    @Column(name = "thumbnail_url", length = 1000)
    private String thumbnailUrl;

    @Column(length = 150)
    private String title;

    @Column(length = 500)
    private String caption;

    @Column(name = "alt_text", length = 100)
    private String altText;

    @Column(length = 60)
    private String category;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    @Builder.Default
    @Column(nullable = false)
    private boolean deleted = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}