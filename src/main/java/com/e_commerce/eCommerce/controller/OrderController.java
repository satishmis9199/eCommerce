package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.dto.ApiResponse;
import com.e_commerce.eCommerce.dto.OrderResponseDto;
import com.e_commerce.eCommerce.dto.OrderTrackingResponseDto;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import com.e_commerce.eCommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RequestMapping("/user/u1/v1")
@RestController
@RequiredArgsConstructor

public class OrderController {
    private final OrderService orderService;

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @GetMapping("/orders/{orderId}/track")
    public ResponseEntity<ApiResponse<OrderResponseDto>> getSpecificOrderDetails(@PathVariable String orderId, @AuthenticationPrincipal CustomUserDetail userDetail) {
        try {
            OrderResponseDto orderResponseDto = orderService.findOrderDetails(orderId, userDetail);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse<>(
                                    true,
                                    "Order Details fetched Successfuully",
                                    orderResponseDto
                            )
                    );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    e.getMessage(), null
                            )
                    );
        }

    }


    @GetMapping(value = "/orders/myOrders", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<OrderResponseDto>>> getMyOrders(@AuthenticationPrincipal CustomUserDetail userDetail) {
        try {
            List<OrderResponseDto> orderResponseDto = orderService.findByOrder(userDetail);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse<>(
                                    true,
                                    "My Details fetched Successfuully",
                                    orderResponseDto
                            )
                    );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    e.getMessage(), null
                            )
                    );
        }

    }

    //    @PreAuthorize("hasRole('USER')")
    @GetMapping("/orders/{orderId}/tracks")

    public ResponseEntity<ApiResponse<OrderTrackingResponseDto>> getTrackingDetail(@AuthenticationPrincipal CustomUserDetail userDetail, @PathVariable String orderId) {
        try {
            OrderTrackingResponseDto orderResponseDto = orderService.getTracking(userDetail, orderId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse<>(
                                    true,
                                    "Tracking details fetched Successfully",
                                    orderResponseDto
                            )
                    );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    e.getMessage(), null
                            )
                    );
        }
    }

    @PostMapping("/orders/{orderId}/cancel")
    public ResponseEntity<ApiResponse<?>> cancelOrder(
            @PathVariable String orderId,
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        try {
            String reason = request.get("reason");

            String message = orderService.cancelOrderByUser(userDetail, reason, orderId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse<>(
                                    true,
                                    message
                            )
                    );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    e.getMessage()
                            )
                    );
        }
    }


    @PostMapping("/orders/{orderId}/return")
    public ResponseEntity<ApiResponse<?>> returnOrder(
            @PathVariable String orderId,
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        try {
            String reason = request.get("reason");

            String message = orderService.returnOrder(userDetail, reason, orderId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse<>(
                                    true,
                                    message
                            )
                    );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    e.getMessage()
                            )
                    );
        }
    }
}
