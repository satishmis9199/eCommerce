package com.e_commerce.eCommerce.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProductDTO {

    private String productId;
    private String name;
    private String brand;

    private String vendorId;
    private String vendorName;
    private String businessName;

    private String image;          // relative path, e.g. "products/cement.jpg"
    private List<String> images;   // relative paths

    private Double rating;
    private Integer reviewCount;

    private BigDecimal price;
    private BigDecimal oldPrice;
    private BigDecimal discountPercent;

    private String stockLevel;     // "in_stock" | "low_stock" | "out_of_stock"
    private String deliveryEta;    // e.g. "Next day", "2-3 days"

    private String description;
    private List<ProductSpecificationResponeDto> productSpecificationResponeDtos;

}