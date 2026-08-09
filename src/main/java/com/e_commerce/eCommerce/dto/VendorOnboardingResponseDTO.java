package com.e_commerce.eCommerce.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({
        "success",
        "profileCompleted",
        "currentStep",
        "data"
})
public class VendorOnboardingResponseDTO {

    private boolean success;
    private boolean profileCompleted;
    private Integer currentStep;
    private String applicationId;
    private VendorOnboardingDataDTO data;

}