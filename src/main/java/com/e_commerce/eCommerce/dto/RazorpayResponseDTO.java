package com.e_commerce.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RazorpayResponseDTO {

    /**
     * Razorpay Key ID
     * Example: rzp_test_xxxxxxxxx
     */
    private String key;

    /**
     * Razorpay Order ID
     * Example: order_QAbCdEfGh12345
     */
    private String orderId;

    /**
     * Amount in paise
     * Example: ₹500.00 = 50000
     */
    private Long amount;

    /**
     * Currency
     * Example: INR
     */
    private String currency;

    /**
     * Your internal order id
     */
    private Long internalOrderId;

    /**
     * Your generated order number
     */
    private String orderNumber;
}