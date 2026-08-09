package com.e_commerce.eCommerce.dto;

import com.e_commerce.eCommerce.entity.DiscountType;
import com.e_commerce.eCommerce.entity.FlashSaleStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FlashSaleRequestDto {

    @NotBlank(message = "Sale name is required")
    @Size(max = 100, message = "Sale name cannot exceed 100 characters")
    private String saleName;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Discount type is required")
    private DiscountType discountType;

    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.01", message = "Discount value must be greater than zero")
    private BigDecimal discountValue;

    @DecimalMin(value = "0.00", message = "Max discount cap cannot be negative")
    private BigDecimal maxDiscountCap;

    @NotNull(message = "Start date/time is required")
    private LocalDateTime startDateTime;

    @NotNull(message = "End date/time is required")
    private LocalDateTime endDateTime;

    @NotNull(message = "Status is required")
    private FlashSaleStatus status;

    @NotEmpty(message = "Please select at least one product")
    @Valid
    private List<FlashSaleItemDto> items;

}