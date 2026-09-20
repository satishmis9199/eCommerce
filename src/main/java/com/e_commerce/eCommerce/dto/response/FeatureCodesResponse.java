package com.e_commerce.eCommerce.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Exact shape the vendor dashboard expects:
 * { "success": true, "features": ["PRODUCT_MANAGEMENT", "FLASH_SALE"] }
 * "message" is only present on errors.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeatureCodesResponse {

    private boolean success;
    private List<String> features;
    private String message;

    public static FeatureCodesResponse ok(List<String> features) {
        return new FeatureCodesResponse(true, features, null);
    }

    public static FeatureCodesResponse error(String message) {
        return new FeatureCodesResponse(false, List.of(), message);
    }
}