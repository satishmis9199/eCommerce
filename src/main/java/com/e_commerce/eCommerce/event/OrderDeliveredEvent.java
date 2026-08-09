package com.e_commerce.eCommerce.event;

import com.e_commerce.eCommerce.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class OrderDeliveredEvent {

    private final String orderNumber;
    private final User user;
    private final String tenantid;

}