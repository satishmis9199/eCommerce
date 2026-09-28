package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.CustomAnnotation.RequiresFeature;
import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.ApiResponse;
import com.e_commerce.eCommerce.dto.response.ShopHistoryResponse;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import com.e_commerce.eCommerce.service.ShopHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/vendor/s2/v1/shop-status/history")
public class ShopHistoryController {

    private final ShopHistoryService shopHistoryService;

    public ShopHistoryController(ShopHistoryService shopHistoryService) {
        this.shopHistoryService = shopHistoryService;
    }

    @GetMapping
    @RequiresFeature("SHOP_HISTORY")
    public ResponseEntity<ApiResponse<?>> getShopHistoryData(
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        String tenantId = TenantContext.getTenantId();

        List<ShopHistoryResponse> shopHistoryResponseList =
                shopHistoryService.getHistory(userDetail, tenantId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        new ApiResponse<>(
                                true,
                                "History Loaded",
                                shopHistoryResponseList
                        )
                );
    }
}