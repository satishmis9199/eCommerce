package com.e_commerce.eCommerce.dto;

import com.e_commerce.eCommerce.dto.SocialMediaDTO;
import com.e_commerce.eCommerce.dto.StorePolicyDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreInfoResponseDTO {

    private Long vendorId;
    private String  tenantId;

    private String businessName;
    private String storeName;
    private String tagline;

    private String logoUrl;
    private String bannerUrl;

    private String themeColor;

    private String supportEmail;
    private String supportPhone;

    private String address;

    private String aboutUs;

    private SocialMediaDTO socialMedia;

    private StorePolicyDTO policies;
}