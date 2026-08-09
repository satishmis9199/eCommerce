package com.e_commerce.eCommerce.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StorePolicyDTO {

    private String shippingPolicy;

    private String returnPolicy;

    private String privacyPolicy;

    private String termsAndConditions;

    private String cancellationPolicy;
}