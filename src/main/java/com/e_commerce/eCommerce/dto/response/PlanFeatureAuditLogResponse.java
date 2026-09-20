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
public class PlanFeatureAuditLogResponse {

    private Long id;
    private String adminEmail;
    private String action;
    private String planCode;
    private String featureCode;
    private String oldValue;
    private String newValue;
    private LocalDateTime createdAt;
}