package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.ApiResponse;

import com.e_commerce.eCommerce.dto.GalleryResponseDto;

import com.e_commerce.eCommerce.dto.request.GalleryRequestDto;
import com.e_commerce.eCommerce.dto.response.GalleryReorderDto;
import com.e_commerce.eCommerce.dto.response.GalleryStatusDto;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import com.e_commerce.eCommerce.service.GalleryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/vendor/s11/v1/gallery")
@RequiredArgsConstructor
public class VendorGalleryController {

    private final GalleryService service;

    @GetMapping
    public ApiResponse<List<GalleryResponseDto>> list() {
        return new ApiResponse<>(true, "Gallery fetched successfully", service.listForVendor(tenant()));
    }

    @PostMapping
    public ApiResponse<GalleryResponseDto> create(@Valid @RequestBody GalleryRequestDto dto,
                                                  @AuthenticationPrincipal CustomUserDetail user) {
        return new ApiResponse<>(true, "Media added successfully", service.create(tenant(), vendorId(user), dto));
    }

    @PutMapping("/{id}")
    public ApiResponse<GalleryResponseDto> update(@PathVariable Long id,
                                                  @Valid @RequestBody GalleryRequestDto dto) {
        return new ApiResponse<>(true, "Media updated successfully", service.update(tenant(), id, dto));
    }

    @PatchMapping("/status")
    public ApiResponse<Void> status(@Valid @RequestBody GalleryStatusDto dto) {
        service.setActive(tenant(), dto.getId(), dto.getActive());
        return new ApiResponse<>(true, "Status updated", null);
    }

    @PatchMapping("/reorder")
    public ApiResponse<Void> reorder(@Valid @RequestBody GalleryReorderDto dto) {
        service.reorder(tenant(), dto.getOrderedIds());
        return new ApiResponse<>(true, "Order saved", null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(tenant(), id);
        return new ApiResponse<>(true, "Media deleted successfully", null);
    }

    private String tenant() {
        return TenantContext.getTenantId();
    }

    // UploadController me bhi getId() String.valueOf se use hota hai, isliye type-safe tareeqa
    private Long vendorId(CustomUserDetail user) {
        return Long.parseLong(String.valueOf(user.getId()));
    }
}