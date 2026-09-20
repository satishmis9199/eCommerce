package com.e_commerce.eCommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * {
 *   "tenantId": "105",
 *   "plan": "PRO",
 *   "features": ["PRODUCT_MANAGEMENT", "SALES_REPORT", ...]
 * }
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantFeaturesResponse {

    private String tenantId;
    private String plan;
    private Set<String> features;
}