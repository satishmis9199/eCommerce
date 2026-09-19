package com.e_commerce.eCommerce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VendorFeatureSettingsRequest {

    private Boolean onlinePaymentEnabled;
    private Boolean codEnabled;
    private Boolean wishlistEnabled;
    private Boolean reviewsEnabled;
    private Boolean chatEnabled;
    private Boolean couponsEnabled;
}