package com.e_commerce.eCommerce.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class OrderCreatedEvent {

    private final Long orderId;

    private final String tenantId;

    private final Long vendorId;

    private final BigDecimal totalAmount;
}