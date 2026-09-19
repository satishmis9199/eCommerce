package com.e_commerce.eCommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorFeatureSettingsResponse {

    private boolean onlinePaymentEnabled;
    private boolean codEnabled;
    private boolean wishlistEnabled;
    private boolean reviewsEnabled;
    private boolean chatEnabled;
    private boolean couponsEnabled;
    private LocalDateTime updatedAt;
}