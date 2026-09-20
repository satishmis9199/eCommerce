package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.dto.ApiResponse;
import com.e_commerce.eCommerce.dto.request.ActiveStatusRequest;
import com.e_commerce.eCommerce.dto.request.PlanRequest;
import com.e_commerce.eCommerce.dto.response.PlanResponse;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import com.e_commerce.eCommerce.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/s1/v1/super/admin/plans")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminPlanController {

    private final PlanService planService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PlanResponse>>> getAllPlans() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Plans fetched successfully", planService.getAllPlans()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PlanResponse>> createPlan(
            @RequestBody PlanRequest request,
            @AuthenticationPrincipal CustomUserDetail admin) {
        try {
            PlanResponse response = planService.createPlan(request, admin);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Plan created successfully", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PlanResponse>> updatePlan(
            @PathVariable Long id,
            @RequestBody PlanRequest request,
            @AuthenticationPrincipal CustomUserDetail admin) {
        try {
            PlanResponse response = planService.updatePlan(id, request, admin);
            return ResponseEntity.ok(new ApiResponse<>(true, "Plan updated successfully", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PatchMapping("/status")
    public ResponseEntity<ApiResponse<PlanResponse>> setStatus(
            @RequestBody ActiveStatusRequest request,
            @AuthenticationPrincipal CustomUserDetail admin) {
        try {
            PlanResponse response = planService.setActive(request, admin);
            return ResponseEntity.ok(new ApiResponse<>(true, "Plan status updated successfully", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}