package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.dto.ApiResponse;
import com.e_commerce.eCommerce.dto.OrderUpdatRequestDTO;
import com.e_commerce.eCommerce.service.AdminOrderService;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vendor")
@Slf4j

@AllArgsConstructor
public class AdminOrderController {
    private final AdminOrderService adminOrderService;
//    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/s2/v1/order/status")
    public ResponseEntity<ApiResponse<String>> updateOrderStatusByAdmin(@AuthenticationPrincipal CustomUserDetail userDetail, @RequestBody OrderUpdatRequestDTO orderUpdatRequestDTO){
        try{
            String message=adminOrderService.updateStatus(userDetail,orderUpdatRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse<>(
                                    true,
                                    "product Updated",
                                    message
                            )
                    );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    e.getMessage(),
                                    e.getMessage()
                            )
                    );
        }
    }

}
