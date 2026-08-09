package com.e_commerce.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartPricingDTO {

    private BigDecimal subtotal;

    private BigDecimal discount;

    private String discountLabel;

    private BigDecimal shipping;

    private String shippingLabel;

    private BigDecimal total;
}