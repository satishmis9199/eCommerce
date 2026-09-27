package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.ApiResponse;
import com.e_commerce.eCommerce.dto.response.VendorDashboardDTO;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import com.e_commerce.eCommerce.service.DashBoardService;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/vendor/u1/v1/data")
public class VendorDashBoardController {
    private final DashBoardService dashBoardService;
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getDashboardData(@AuthenticationPrincipal CustomUserDetail userDetail){
        String tenantId= TenantContext.getTenantId();
        VendorDashboardDTO vendorDashboardDTO=dashBoardService.getDashBoardData(userDetail,tenantId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Dashboard Loaded SuccessFully",
                                vendorDashboardDTO
                        )
                );

    }
}
