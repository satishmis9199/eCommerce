package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.dto.ApiResponse;
import com.e_commerce.eCommerce.dto.request.AdApproveRequestDto;
import com.e_commerce.eCommerce.dto.request.AdRejectRequestDto;
import com.e_commerce.eCommerce.dto.response.AdResponseDto;
import com.e_commerce.eCommerce.service.AdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/s1/v1/ads")
@RequiredArgsConstructor
public class AdminAdController {

    private final AdService adService;

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<AdResponseDto>>> getPendingAds() {
        List<AdResponseDto> response = adService.getPendingAds();
        return ResponseEntity.ok(new ApiResponse<>(true, "Pending ads fetched successfully", response));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<Void>> approveAd(
            @PathVariable Long id,
            @RequestBody(required = false) AdApproveRequestDto dto) {

        Integer activeDays = dto != null ? dto.getActiveDays() : null;
        boolean updated = adService.approveAd(id, activeDays);

        if (!updated) {
            return ResponseEntity.status(404).body(new ApiResponse<>(false, "Ad not found", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Ad approved and is now live", null));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectAd(
            @PathVariable Long id,
            @RequestBody AdRejectRequestDto dto) {

        boolean updated = adService.rejectAd(id, dto.getReason());

        if (!updated) {
            return ResponseEntity.status(404).body(new ApiResponse<>(false, "Ad not found", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Ad rejected", null));
    }
}