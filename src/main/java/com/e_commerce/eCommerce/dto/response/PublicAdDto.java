package com.e_commerce.eCommerce.dto.response;

import com.e_commerce.eCommerce.entity.Ad;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicAdDto {
    private Long id;
    private String title;
    private String imageUrl;
    private String targetUrl;
    private String slotKey;
    private String subtext;
    private String ctaLabel;
    private String advertiserName;
    private boolean internal;
    private boolean dismissible;

    public static PublicAdDto from(Ad ad) {
        return PublicAdDto.builder()
                .id(ad.getId())
                .title(ad.getTitle())
                .imageUrl(ad.getImageUrl())
                .targetUrl(ad.getTargetUrl())
                .slotKey(ad.getSlotKey() == null ? null : ad.getSlotKey().name())
                .subtext(ad.getSubtext())
                .ctaLabel(ad.getCtaLabel())
                .advertiserName(ad.getAdvertiserName())
                .internal(ad.isInternal())
                .dismissible(ad.isDismissible())
                .build();
    }
}