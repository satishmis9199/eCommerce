package com.e_commerce.eCommerce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorBrandingRequestDTO {

    private String primaryColor;

    private String secondaryColor;

    private String logoUrl;

    private String bannerUrl;

    private String faviconUrl;
}