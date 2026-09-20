package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.dto.ApiResponse;
import com.e_commerce.eCommerce.dto.request.PlanFeatureMappingRequest;
import com.e_commerce.eCommerce.dto.response.PlanFeatureMappingResponse;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import com.e_commerce.eCommerce.service.PlanFeatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/s1/v1/super/admin/plan-features")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminPlanFeatureController {

    private final PlanFeatureService planFeatureService;
    @GetMapping("/{planId}")
    public ResponseEntity<ApiResponse<PlanFeatureMappingResponse>> getMapping(@PathVariable Long planId) {
        try {
            PlanFeatureMappingResponse response = planFeatureService.getMapping(planId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Plan feature mapping fetched successfully", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping
    public ResponseEntity<ApiResponse<PlanFeatureMappingResponse>> saveMapping(
            @RequestBody PlanFeatureMappingRequest request,
            @AuthenticationPrincipal CustomUserDetail admin) {
        try {
            PlanFeatureMappingResponse response = planFeatureService.saveMapping(request, admin);
            return ResponseEntity.ok(new ApiResponse<>(true, "Plan feature mapping saved successfully", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}