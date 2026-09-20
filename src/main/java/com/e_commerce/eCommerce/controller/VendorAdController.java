package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.CustomAnnotation.RequiresFeature;
import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.ApiResponse;
import com.e_commerce.eCommerce.dto.request.AdRequestDto;
import com.e_commerce.eCommerce.dto.response.AdResponseDto;
import com.e_commerce.eCommerce.service.AdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/vendor/s2/v1/ads")
@RequiredArgsConstructor
public class VendorAdController {

    private final AdService adService;
    @RequiresFeature("SPONSORED_ADS")
    @PostMapping
    public ResponseEntity<ApiResponse<AdResponseDto>> requestAd(@RequestBody AdRequestDto dto) {
        String tenantId = resolveTenantId();
        AdResponseDto response = adService.createAdRequest(tenantId, dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Ad request sent for approval", response));
    }
    @RequiresFeature("SPONSORED_ADS")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AdResponseDto>>> getMyAds() {
        String tenantId = resolveTenantId();
        List<AdResponseDto> response = adService.getVendorAds(tenantId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Ad requests fetched successfully", response));
    }
    @RequiresFeature("SPONSORED_ADS")
    private String resolveTenantId() {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalStateException("Tenant ID not found for authenticated vendor");
        }
        return tenantId;
    }
}