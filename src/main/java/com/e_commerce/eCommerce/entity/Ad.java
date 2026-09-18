package com.e_commerce.eCommerce.entity;

import com.e_commerce.eCommerce.enums.AdSlotKey;
import com.e_commerce.eCommerce.enums.AdStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "ads",
        indexes = {

                @Index(name = "idx_ad_serving", columnList = "slot_key, status, expires_at"),
                @Index(name = "idx_ad_tenant", columnList = "tenant_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tenantId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String targetUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdStatus status;

    private String rejectionReason;

    private LocalDateTime createdAt;

    private LocalDateTime approvedAt;

    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "slot_key", length = 40)
    private AdSlotKey slotKey;


    @Column(length = 250)
    private String subtext;

    @Column(length = 40)
    private String ctaLabel;
    @Column(length = 120)
    private String advertiserName;

    @Column(nullable = false)
    @Builder.Default
    private boolean internal = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean dismissible = false;


    @Column(nullable = false)
    @Builder.Default
    private Integer priority = 0;


    @Column(nullable = false)
    @Builder.Default
    private Long impressions = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long clicks = 0L;
}