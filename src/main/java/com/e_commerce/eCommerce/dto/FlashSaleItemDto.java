package com.e_commerce.eCommerce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FlashSaleItemDto {

    @NotNull(message = "Product Id is required")
    private Long productId;

    @NotNull(message = "Sale price is required")
    @DecimalMin(value = "0.00", message = "Sale price cannot be negative")
    private BigDecimal salePrice;

}