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
public class CartItemResponseDTO {

    private Long itemId;

    private Long productId;

    private String name;

    private String image;

    private String businessName;

    private String brand;

    private BigDecimal unitPrice;

    private Integer qty;

    private BigDecimal lineTotal;
}