package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.dto.ApiResponse;

import com.e_commerce.eCommerce.dto.BannerRequestDto;
import com.e_commerce.eCommerce.dto.BannerResponseDto;
import com.e_commerce.eCommerce.dto.CustomerDetailDTo;
import com.e_commerce.eCommerce.service.BannerService;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class BannerController {
    private final BannerService bannerService;
    @PostMapping("/vendor/s11/v1/banner")
    public ResponseEntity<ApiResponse<?>> saveBanner(@AuthenticationPrincipal CustomUserDetail customerDetailDTo, @RequestBody BannerRequestDto bannerRequestDTO){
        try{
            String messaage=bannerService.saveBanner(customerDetailDTo,bannerRequestDTO);
            return ResponseEntity.status(
                    HttpStatus.CREATED
            )
                    .body(
                            new ApiResponse<>(
                                    true,
                                    "Successfully saved",
                                    messaage
                            )
                    );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    "Unable To fetch",
                                    e.getMessage()
                            )
                    );
        }
    }
    @GetMapping("/vendor/s11/v1/banner")
    public ResponseEntity<ApiResponse<List<BannerResponseDto>>> getBannerAdmin(@AuthenticationPrincipal CustomUserDetail customUserDetail){
        try{
            List<BannerResponseDto> banner=bannerService.loadBbanner(customUserDetail);
            return ResponseEntity.status(
                            HttpStatus.CREATED
                    )
                    .body(
                            new ApiResponse<>(
                                    true,
                                    "Successfully saved",
                                    banner
                            )
                    );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    e.getMessage(),
                                    null
                            )
                    );
        }
    }
}
