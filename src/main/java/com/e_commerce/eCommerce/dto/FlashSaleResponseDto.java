package com.e_commerce.eCommerce.dto;


import com.e_commerce.eCommerce.entity.DiscountType;
import com.e_commerce.eCommerce.entity.FlashSaleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashSaleResponseDto {

    private Long id;

    private String saleName;

    private String description;

    private DiscountType discountType;

    private BigDecimal discountValue;

    private BigDecimal maxDiscountCap;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private FlashSaleStatus status;

    private List<FlashSaleItemResponseDto> items;

}