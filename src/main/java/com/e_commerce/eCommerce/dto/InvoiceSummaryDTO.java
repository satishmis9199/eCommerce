package com.e_commerce.eCommerce.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class InvoiceSummaryDTO {

    private BigDecimal subtotal;
    private BigDecimal totalDiscount;
    private BigDecimal shippingCharge;
    private BigDecimal platformCharge;
    private BigDecimal totalTax;
    private BigDecimal grandTotal;
    private BigDecimal amountPaid;
    private BigDecimal amountDue;

}