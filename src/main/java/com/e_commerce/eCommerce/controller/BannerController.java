package com.e_commerce.eCommerce.controller;
import com.e_commerce.eCommerce.dto.ApiResponse;
import com.e_commerce.eCommerce.dto.BannerRequestDto;
import com.e_commerce.eCommerce.dto.BannerResponseDto;
import com.e_commerce.eCommerce.dto.request.BannerStatusRequestDTO;
import com.e_commerce.eCommerce.service.BannerService;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@AllArgsConstructor
public class BannerController {
    private final BannerService bannerService;
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/vendor/s11/v1/banner")
    public ResponseEntity<ApiResponse<?>> saveBanner(@AuthenticationPrincipal CustomUserDetail customerDetailDTo, @RequestBody BannerRequestDto bannerRequestDTO) {
        try {
            String messaage = bannerService.saveBanner(customerDetailDTo, bannerRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Successfully saved", messaage));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, "Unable To fetch", e.getMessage()));
        }
    }
    @GetMapping("/vendor/s11/v1/banner")
    public ResponseEntity<ApiResponse<List<BannerResponseDto>>> getBannerAdmin(@AuthenticationPrincipal CustomUserDetail customUserDetail) {
        try {
            List<BannerResponseDto> banner = bannerService.loadBbanner(customUserDetail);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Successfully saved", banner));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/vendor/s11/v1/banner/{id}")
    public ResponseEntity<ApiResponse<?>> updateBanner(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetail customerDetailDTo, @RequestBody BannerRequestDto bannerRequestDTO) {
        try {
            String messaage = bannerService.updateBanner(customerDetailDTo, bannerRequestDTO, id);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Successfully saved", messaage));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, "Unable To fetch", e.getMessage()));
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/vendor/s11/v1/banner/status")
    public ResponseEntity<ApiResponse<?>> changeBannerStatus(@RequestBody BannerStatusRequestDTO request, @AuthenticationPrincipal CustomUserDetail userDetail) {


        try {
            String message = bannerService.changeBannerStatus(userDetail, request.getId(), request.getActive());
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Successfully saved", message));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, "Unable To fetch", e.getMessage()));
        }
    }
}
