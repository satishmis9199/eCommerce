package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.CustomAnnotation.RequiresFeature;
import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.ApiResponse;
import com.e_commerce.eCommerce.dto.response.ShopHistoryResponse;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import com.e_commerce.eCommerce.service.ShopHistoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/vendor/s2/v1/shop-status/history")
@AllArgsConstructor
public class ShopHistoryController {
    private final ShopHistoryService shopHistoryService;
    @GetMapping
    @RequiresFeature("SHOP_HISTORY")
    private ResponseEntity<ApiResponse<?>> getShopHistoryData(@AuthenticationPrincipal CustomUserDetail userDetail){
     String tenantid= TenantContext.getTenantId();
        List<ShopHistoryResponse> shopHistoryResponseList=shopHistoryService.getHistory( userDetail,tenantid);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "History Loaded",
                                shopHistoryResponseList
                        )

        );
    }
}
