package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.ApiResponse;
import com.e_commerce.eCommerce.dto.response.FeatureSettingsDTO;
import com.e_commerce.eCommerce.service.FeatureSettingsService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/u1/v1")
public class FeatureSettingsController {
    private final FeatureSettingsService featureSettingsService;
    @GetMapping("/featureSettings")
    public ResponseEntity<ApiResponse<FeatureSettingsDTO>> getFeature(){
        String tenantId= TenantContext.getTenantId();
        FeatureSettingsDTO featureSettingsDTO=featureSettingsService.getFeatureDetails(tenantId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>
                                (
                                        true,
                                        "Feature Deatils Loaded",
                                        featureSettingsDTO

                                )
                );
    }

}
