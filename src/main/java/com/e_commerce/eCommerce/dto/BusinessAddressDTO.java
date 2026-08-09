package com.e_commerce.eCommerce.dto;

import lombok.Data;

@Data
public class BusinessAddressDTO {

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String pincode;

}