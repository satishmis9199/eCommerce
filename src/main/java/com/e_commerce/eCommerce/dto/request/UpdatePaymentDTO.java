package com.e_commerce.eCommerce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePaymentDTO {
    private String orderNo;
    private BigDecimal paymentCollectedAmount;
}
