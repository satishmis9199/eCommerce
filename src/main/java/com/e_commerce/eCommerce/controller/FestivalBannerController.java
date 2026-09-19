package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.dto.ApiResponse;

import com.e_commerce.eCommerce.dto.request.FestivalBannerRequestDto;
import com.e_commerce.eCommerce.dto.request.FestivalBannerStatusRequestDTO;
import com.e_commerce.eCommerce.dto.response.FestivalBannerResponseDto;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import com.e_commerce.eCommerce.service.FestivalBannerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class FestivalBannerController {

    private final FestivalBannerService festivalBannerService;


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/vendor/s11/v1/festival-banner")
    public ResponseEntity<ApiResponse<?>> saveFestivalBanner(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @RequestBody FestivalBannerRequestDto festivalBannerRequestDTO) {

        try {

            String message = festivalBannerService.saveFestivalBanner(
                    customUserDetail,
                    festivalBannerRequestDTO
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            true,
                            "Successfully saved",
                            message
                    ));

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            false,
                            "Unable To fetch",
                            e.getMessage()
                    ));
        }
    }


    @GetMapping("/vendor/s11/v1/festival-banner")
    public ResponseEntity<ApiResponse<List<FestivalBannerResponseDto>>> getFestivalBannerAdmin(
            @AuthenticationPrincipal CustomUserDetail customUserDetail) {

        try {
            List<FestivalBannerResponseDto> festivalBanner =
                    festivalBannerService.loadFestivalBanner(customUserDetail);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            true,
                            "Successfully saved",
                            festivalBanner
                    ));

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            false,
                            e.getMessage(),
                            null
                    ));
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/vendor/s11/v1/festival-banner/{id}")
    public ResponseEntity<ApiResponse<?>> updateFestivalBanner(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @RequestBody FestivalBannerRequestDto festivalBannerRequestDTO) {

        try {

            String message = festivalBannerService.updateFestivalBanner(
                    customUserDetail,
                    festivalBannerRequestDTO,
                    id
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            true,
                            "Successfully saved",
                            message
                    ));

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            false,
                            "Unable To fetch",
                            e.getMessage()
                    ));
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/vendor/s11/v1/festival-banner/status")
    public ResponseEntity<ApiResponse<?>> changeFestivalBannerStatus(
            @RequestBody FestivalBannerStatusRequestDTO request,
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        try {

            String message =
                    festivalBannerService.changeFestivalBannerStatus(
                            userDetail,
                            request.getId(),
                            request.getActive()
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            true,
                            "Successfully saved",
                            message
                    ));

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            false,
                            "Unable To fetch",
                            e.getMessage()
                    ));
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/vendor/s11/v1/festival-banner/{id}")
    public ResponseEntity<ApiResponse<?>> deleteFestivalBanner(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetail customUserDetail) {

        try {

            String message = festivalBannerService.deleteFestivalBanner(
                    customUserDetail,
                    id
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            true,
                            message,
                            message
                    ));

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            false,
                            "Unable To fetch",
                            e.getMessage()
                    ));
        }
    }
}