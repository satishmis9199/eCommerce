package com.e_commerce.eCommerce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderNotificationDTO {

    private Long orderId;

    private String tenantId;

    private Long vendorId;

    private BigDecimal totalAmount;

    private String type;

    private String title;

    private String message;
}