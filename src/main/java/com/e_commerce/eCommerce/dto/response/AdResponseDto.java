package com.e_commerce.eCommerce.dto.response;

import com.e_commerce.eCommerce.entity.Ad;
import com.e_commerce.eCommerce.enums.AdStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdResponseDto {

    private Long id;
    private String tenantId;
    private String title;
    private String imageUrl;
    private String targetUrl;
    private AdStatus status;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private LocalDateTime expiresAt;


    private String slotKey;
    private String slotDescription;
    private String subtext;
    private String ctaLabel;
    private String advertiserName;
    private boolean internal;
    private boolean dismissible;
    private Integer priority;

    private Long impressions;
    private Long clicks;
    private Double ctr;
    private boolean liveNow;

    public static AdResponseDto from(Ad ad) {
        long imp = ad.getImpressions() == null ? 0L : ad.getImpressions();
        long clk = ad.getClicks() == null ? 0L : ad.getClicks();

        boolean live = ad.getStatus() == AdStatus.APPROVED
                && (ad.getExpiresAt() == null || ad.getExpiresAt().isAfter(LocalDateTime.now()));

        return AdResponseDto.builder()
                .id(ad.getId())
                .tenantId(ad.getTenantId())
                .title(ad.getTitle())
                .imageUrl(ad.getImageUrl())
                .targetUrl(ad.getTargetUrl())
                .status(ad.getStatus())
                .rejectionReason(ad.getRejectionReason())
                .createdAt(ad.getCreatedAt())
                .approvedAt(ad.getApprovedAt())
                .expiresAt(ad.getExpiresAt())
                .slotKey(ad.getSlotKey() == null ? null : ad.getSlotKey().name())
                .slotDescription(ad.getSlotKey() == null
                        ? "House ad (no fixed slot)"
                        : ad.getSlotKey().getDescription())
                .subtext(ad.getSubtext())
                .ctaLabel(ad.getCtaLabel())
                .advertiserName(ad.getAdvertiserName())
                .internal(ad.isInternal())
                .dismissible(ad.isDismissible())
                .priority(ad.getPriority())
                .impressions(imp)
                .clicks(clk)
                .ctr(imp == 0 ? 0.0 : Math.round(clk * 10000.0 / imp) / 100.0)
                .liveNow(live)
                .build();
    }
}