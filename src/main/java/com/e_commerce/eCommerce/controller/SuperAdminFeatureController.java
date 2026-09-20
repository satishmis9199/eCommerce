package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.dto.ApiResponse;
import com.e_commerce.eCommerce.dto.request.ActiveStatusRequest;
import com.e_commerce.eCommerce.dto.request.FeatureRequest;
import com.e_commerce.eCommerce.dto.response.FeatureResponse;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import com.e_commerce.eCommerce.service.FeatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/s1/v1/super/admin/features")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminFeatureController {

    private final FeatureService featureService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FeatureResponse>>> getAllFeatures() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Features fetched successfully", featureService.getAllFeatures()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FeatureResponse>> createFeature(
            @RequestBody FeatureRequest request,
            @AuthenticationPrincipal CustomUserDetail admin) {
        try {
            FeatureResponse response = featureService.createFeature(request, admin);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Feature created successfully", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FeatureResponse>> updateFeature(
            @PathVariable Long id,
            @RequestBody FeatureRequest request,
            @AuthenticationPrincipal CustomUserDetail admin) {
        try {
            FeatureResponse response = featureService.updateFeature(id, request, admin);
            return ResponseEntity.ok(new ApiResponse<>(true, "Feature updated successfully", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PatchMapping("/status")
    public ResponseEntity<ApiResponse<FeatureResponse>> setStatus(
            @RequestBody ActiveStatusRequest request,
            @AuthenticationPrincipal CustomUserDetail admin) {
        try {
            FeatureResponse response = featureService.setActive(request, admin);
            return ResponseEntity.ok(new ApiResponse<>(true, "Feature status updated successfully", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}