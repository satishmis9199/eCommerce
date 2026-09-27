package com.e_commerce.eCommerce.dto.request;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductFilterDTO {
    private List<Long> category;
    private List<String> brand;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer rating;
}