package com.e_commerce.eCommerce.controller;


import com.e_commerce.eCommerce.CustomAnnotation.RequiresFeature;
import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.ApiResponse;
import com.e_commerce.eCommerce.dto.request.VendorFeatureSettingsRequest;
import com.e_commerce.eCommerce.dto.response.VendorFeatureSettingsResponse;
import com.e_commerce.eCommerce.service.VendorFeatureSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vendor/settings/features")
@RequiredArgsConstructor
public class VendorFeatureSettingsController {

    private final VendorFeatureSettingsService service;
    @RequiresFeature("SETTINGS")
    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<VendorFeatureSettingsResponse>> getFeatures() {
        VendorFeatureSettingsResponse data = service.get(currentTenantId(), currentVendorId(currentTenantId()));
        return ResponseEntity.ok(new ApiResponse<>(true, "Store features fetched successfully", data));
    }
    @RequiresFeature("SETTINGS")
    @PutMapping
    public ResponseEntity<ApiResponse<VendorFeatureSettingsResponse>> updateFeatures(
            @RequestBody VendorFeatureSettingsRequest request) {
        try {
            VendorFeatureSettingsResponse data = service.update(currentTenantId(), currentVendorId(currentTenantId()), request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Store features saved successfully", data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    private String currentTenantId() {
        return TenantContext.getTenantId();
    }

    private Long currentVendorId(String tenantId) {
        return service.findVendorId(tenantId);
    }
}