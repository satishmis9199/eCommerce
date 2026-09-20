package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.CustomAnnotation.RequiresFeature;
import com.e_commerce.eCommerce.dto.ApiResponse;
import com.e_commerce.eCommerce.dto.response.PublicAdDto;
import com.e_commerce.eCommerce.enums.AdSlotKey;
import com.e_commerce.eCommerce.service.AdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/u1/v1/ads")
@RequiredArgsConstructor
public class PublicAdController {

    private final AdService adService;


    @GetMapping("/random")
    @RequiresFeature("SPONSORED_ADS")
    public ResponseEntity<ApiResponse<PublicAdDto>> getRandomAd() {
        PublicAdDto ad = adService.getRandomActiveAd();
        if (ad == null) {
            return ResponseEntity.ok(new ApiResponse<>(true, "No active ads", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Random ad fetched", ad));
    }
    @RequiresFeature("SPONSORED_ADS")
    @GetMapping("/slots/{slotKey}")
    public ResponseEntity<ApiResponse<PublicAdDto>> getAdForSlot(@PathVariable String slotKey) {

        AdSlotKey key;
        try {
            key = AdSlotKey.valueOf(slotKey.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Unknown ad slot", null));
        }

        PublicAdDto ad = adService.getAdForSlot(key);
        if (ad == null) {
            return ResponseEntity.ok(new ApiResponse<>(true, "No active ad for this slot", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Ad fetched", ad));
    }
    @RequiresFeature("SPONSORED_ADS")
    @PostMapping("/{adId}/impression")
    public ResponseEntity<ApiResponse<Void>> recordImpression(@PathVariable Long adId) {
        adService.recordImpression(adId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Impression recorded", null));
    }
    @RequiresFeature("SPONSORED_ADS")
    @PostMapping("/{adId}/click")
    public ResponseEntity<ApiResponse<Void>> recordClick(@PathVariable Long adId) {
        adService.recordClick(adId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Click recorded", null));
    }
}