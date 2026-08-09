package com.e_commerce.eCommerce.dto;

import com.e_commerce.eCommerce.enums.PolicyStatus;
import com.e_commerce.eCommerce.enums.PolicyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VendorPolicyRequestDto {

    @NotNull(message = "Policy type is required")
    private PolicyType policyType;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Status is required")
    private PolicyStatus status;
}