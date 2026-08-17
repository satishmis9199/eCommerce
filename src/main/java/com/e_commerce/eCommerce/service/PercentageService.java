package com.e_commerce.eCommerce.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class PercentageService {
    public BigDecimal findTotalTotalPrice(int quantity, BigDecimal sellingPrice) {
        return sellingPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
