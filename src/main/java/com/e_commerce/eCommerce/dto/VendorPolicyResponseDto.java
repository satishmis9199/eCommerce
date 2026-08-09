package com.e_commerce.eCommerce.dto;

import com.e_commerce.eCommerce.enums.PolicyStatus;
import com.e_commerce.eCommerce.enums.PolicyType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VendorPolicyResponseDto {
    private Long id;
    private PolicyType policyType;
    private String title;
    private String content;
    private PolicyStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}