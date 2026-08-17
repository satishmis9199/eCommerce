package com.e_commerce.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderTrackingResponseDto {

    private String orderId;
    private String currentStatus;
    private String returnStatus;// "placed" | "confirmed" | "shipped" | "out_for_delivery" | "delivered" | "cancelled"
    private String courierName;
    private String trackingNumber;
    private String estimatedDelivery;   // yyyy-MM-dd format string
    private List<OrderTrackingHistoryDto> history;
    private List<ReturnTrackingHistory> returnHistory;

}