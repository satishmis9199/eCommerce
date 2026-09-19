package com.e_commerce.eCommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class FeatureSettingsDTO {
    private boolean onlinePaymentEnabled;
    private boolean codEnabled;
    private boolean wishlistEnabled;
    private boolean reviewsEnabled;
}
