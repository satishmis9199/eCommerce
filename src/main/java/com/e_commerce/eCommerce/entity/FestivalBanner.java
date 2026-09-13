package com.e_commerce.eCommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "festival_banners",
        indexes = {
                @Index(name = "idx_festival_banner_tenant", columnList = "tenant_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class FestivalBanner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false, length = 255)
    private String tenantId;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "message", length = 500)
    private String message;

    @Column(name = "banner_image", length = 500)
    private String bannerImage;

    @Column(name = "start_at")
    private LocalDateTime startAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @Column(name = "active", nullable = false)
    private boolean active = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;
}