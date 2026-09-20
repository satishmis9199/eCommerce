package com.e_commerce.eCommerce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanFeatureMappingRequest {

    private Long planId;
    private List<FeatureToggle> features;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeatureToggle {
        private Long featureId;
        private Boolean enabled;
    }
}