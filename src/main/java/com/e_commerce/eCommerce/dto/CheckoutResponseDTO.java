package com.e_commerce.eCommerce.dto;

import com.e_commerce.eCommerce.entity.OrderStatus;
import com.e_commerce.eCommerce.entity.PaymentMethod;
import com.e_commerce.eCommerce.entity.PaymentStatus;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutResponseDTO {

    private Long orderId;

    private String orderNumber;

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private OrderStatus orderStatus;

    private RazorpayResponseDTO razorpay;
}