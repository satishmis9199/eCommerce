package com.e_commerce.eCommerce.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderTrackingEvent {
    private Long orderId;
    private String tenantId;
    private Long vendorId;
}
