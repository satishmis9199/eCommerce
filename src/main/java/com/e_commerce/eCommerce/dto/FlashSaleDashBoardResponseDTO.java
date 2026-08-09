package com.e_commerce.eCommerce.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class FlashSaleDashBoardResponseDTO {
    private Long startedAt;   // epoch millis, e.g. 1754140800000
    private Long endsAt;      // epoch millis
    private List<ProductDTO> products;

    }

