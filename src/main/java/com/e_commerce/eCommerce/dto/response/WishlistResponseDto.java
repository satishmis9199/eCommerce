package com.e_commerce.eCommerce.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class WishlistResponseDto {

    private Long id;

    private Long productId;

    private String productName;

    private String productImage;

    private BigDecimal price;

    private LocalDateTime createdAt;
}