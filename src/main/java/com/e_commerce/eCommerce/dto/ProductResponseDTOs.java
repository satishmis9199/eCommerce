package com.e_commerce.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTOs {

    private Long productId;

    private String name;

    private String brand;

    private Long vendorId;

    private String businessName;

    private String image;

    private List<String> images;

    private Double rating;

    private Integer reviewCount;

    private BigDecimal price;

    private BigDecimal oldPrice;

    private BigDecimal discountPercent;

    private String stockLevel;

    private String deliveryEta;

    private String description;

    /**
     * JSON format:
     * [
     * ["Brand", "Kumar Traders"],
     * ["Grade", "A"],
     * ["Weight", "50 KG"]
     * ]
     */
    private List<ProductSpecificationResponeDto> productSpecificationResponeDtos;
}