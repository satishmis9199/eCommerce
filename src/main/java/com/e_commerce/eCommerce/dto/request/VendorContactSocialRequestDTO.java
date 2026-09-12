package com.e_commerce.eCommerce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorContactSocialRequestDTO {

    private String supportEmail;

    private String supportPhone;

    private String whatsappNumber;

    private String website;

    private String facebook;

    private String instagram;

    private String linkedin;

    private String youtube;
}