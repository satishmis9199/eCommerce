package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.response.FeatureCodesResponse;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import com.e_commerce.eCommerce.service.PlanFeatureService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/vendor/s1/v1/plan")
@RequiredArgsConstructor
public class VendorPlanFeatureController {

    private final PlanFeatureService vendorFeatureService;

    @GetMapping("/features")
    public ResponseEntity<FeatureCodesResponse> getMyFeatures(
            @AuthenticationPrincipal CustomUserDetail vendor) {

        try {
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noStore())
                    .body(
                            FeatureCodesResponse.ok(
                                    vendorFeatureService.getEnabledFeatureCodes(
                                            vendor,
                                            TenantContext.getTenantId()
                                    )
                            )
                    );

        } catch (Exception e) {
            log.error("Could not load plan features", e);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            FeatureCodesResponse.error(
                                    "Could not load plan features"
                            )
                    );
        }
    }
}