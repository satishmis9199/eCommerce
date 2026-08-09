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
public class FlashSaleItemResponseDto {

    private Long productId;

    private String productName;

    private BigDecimal originalPrice;

    private BigDecimal salePrice;

}