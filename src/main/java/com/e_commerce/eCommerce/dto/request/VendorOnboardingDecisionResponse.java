package com.e_commerce.eCommerce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VendorOnboardingDecisionResponse {

    private boolean success;
    private String message;
    private String status;

    public static VendorOnboardingDecisionResponse ok(String message, String status) {
        return new VendorOnboardingDecisionResponse(true, message, status);
    }

    public static VendorOnboardingDecisionResponse error(String message) {
        return new VendorOnboardingDecisionResponse(false, message, null);
    }
}