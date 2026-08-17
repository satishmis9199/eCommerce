package com.e_commerce.eCommerce.dto;

import com.e_commerce.eCommerce.entity.PaymentMethod;
import com.e_commerce.eCommerce.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class OrderResponseDto {

    private String orderId;
    private Long orderNo;
    private String date;
    private String customer;
    private String status;
    private BigDecimal total;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private List<OrderItemDTo> orderItemDToList;

}
