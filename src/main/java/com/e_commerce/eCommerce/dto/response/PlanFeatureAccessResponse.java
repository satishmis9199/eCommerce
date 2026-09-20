package com.e_commerce.eCommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * One row of:
 * Feature                  Access
 * ---------------------------------
 * Product Management        [x]
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanFeatureAccessResponse {

    private Long featureId;
    private String featureCode;
    private String featureName;
    private boolean featureActive;
    private boolean enabled;
}