package com.e_commerce.eCommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Every feature in the system with its enabled flag for the given plan.
 * Features that are not mapped yet are returned with enabled = false.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanFeatureMappingResponse {

    private Long planId;
    private String planName;
    private List<FeatureMapping> features;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeatureMapping {
        private Long featureId;
        private String code;
        private String name;
        private boolean enabled;
    }
}