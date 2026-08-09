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
public class ProductCardResponseDTO {

    private Long productId;

    private String name;

    private String image;

    private String businessName;

    private String brand;

    private Double rating;

    private Integer reviewCount;

    private BigDecimal price;

    private BigDecimal oldPrice;

    private Integer discountPercent;


    private String stockLevel;

    private String deliveryEta;

//    added new
    private String description;
    private String specifications;
    private Long vendorId;

}