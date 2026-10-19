package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.ApiResponse;
import com.e_commerce.eCommerce.dto.GalleryResponseDto;
import com.e_commerce.eCommerce.enums.MediaType;

import com.e_commerce.eCommerce.service.PublicGalleryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/u1/v1/gallery")
@RequiredArgsConstructor
public class PublicGalleryController {

    private final PublicGalleryService galleryService;

    @GetMapping
    public ApiResponse<Page<GalleryResponseDto>> list(
            @RequestParam(required = false) MediaType type,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        String tenantId= TenantContext.getTenantId();

        return new ApiResponse<>(true, "Gallery fetched successfully",
                galleryService.page(tenantId,type, category, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<GalleryResponseDto> one(@PathVariable Long id) {
        return new ApiResponse<>(true, "OK", galleryService.byId(TenantContext.getTenantId(),id));
    }
}