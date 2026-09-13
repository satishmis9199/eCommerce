package com.e_commerce.eCommerce.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreInfoResponseDTO {

    private Long vendorId;
    private String tenantId;
    private String businessName;
    private String storeName;
    private String tagline;
    private String storeType;
    private String faviconUrl;
    private String logoUrl;
    private String bannerUrl;
    private String themeColor;
    private String supportEmail;
    private String supportPhone;

    private String address;

    private String aboutUs;

    private SocialMediaDTO socialMedia;

    private StorePolicyDTO policies;
    private String festiveImageUrl;
    private boolean isFestive;
}