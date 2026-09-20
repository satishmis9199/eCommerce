package com.e_commerce.eCommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanFeatureMatrixResponse {

    private List<PlanColumn> plans;
    private List<FeatureRow> features;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanColumn {
        private Long planId;
        private String planCode;
        private String planName;
        private boolean planActive;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeatureRow {
        private Long featureId;
        private String featureCode;
        private String featureName;
        private boolean featureActive;
        /** keyed by planId -> enabled */
        private Map<Long, Boolean> access;
    }
}