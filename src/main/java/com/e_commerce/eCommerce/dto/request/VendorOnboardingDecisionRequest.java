package com.e_commerce.eCommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class VendorOnboardingDecisionRequest {

    @NotNull(message = "applicationId is required")
    private Long applicationId;

    @NotBlank(message = "action is required")
    private String action;

    @NotBlank(message = "remarks are required")
    private String remarks;

    private boolean allowResubmit;
}