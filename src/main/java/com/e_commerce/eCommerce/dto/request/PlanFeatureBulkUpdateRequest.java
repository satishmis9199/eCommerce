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
public class PlanFeatureBulkUpdateRequest {

    private Long planId;
    private List<FeatureAccessEntry> features;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeatureAccessEntry {
        private Long featureId;
        private Boolean enabled;
    }
}