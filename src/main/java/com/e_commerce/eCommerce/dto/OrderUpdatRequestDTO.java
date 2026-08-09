package com.e_commerce.eCommerce.dto;

import com.e_commerce.eCommerce.entity.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class OrderUpdatRequestDTO
{
    private String orderId;
    private String status;
    private String remarks;
    private String location;

}
