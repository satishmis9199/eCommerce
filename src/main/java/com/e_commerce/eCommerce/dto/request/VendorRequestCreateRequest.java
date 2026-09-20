package com.e_commerce.eCommerce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VendorRequestCreateRequest {

    private String businessName;
    private String ownerName;
    private String phone;
    private String email;
    private String city;
    private String state;
    private String businessCategory;
    private String message;
}