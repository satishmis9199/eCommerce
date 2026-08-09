package com.e_commerce.eCommerce.dto;

import lombok.Data;

@Data
public class VendorOnboardingDataDTO {

    private BasicInfoDto basic;
    private BusinessDetailsDTO business;
    private BusinessAddressDTO address;
    private BankInfoDto bank;
    private BrandingDTO branding;

}