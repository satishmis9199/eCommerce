package com.e_commerce.eCommerce.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class BillingAddressDto {

    private String line1;
    private String line2;
    private String state;
    private String city;
    private String pincode;
    private String landmark;
}
