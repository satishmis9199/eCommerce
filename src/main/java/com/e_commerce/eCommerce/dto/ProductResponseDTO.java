package com.e_commerce.eCommerce.dto;

import com.e_commerce.eCommerce.entity.ProductStatus;
import com.e_commerce.eCommerce.entity.ProductUnit;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ProductResponseDTO {





    private Long id;

    private Long categoryId;

    private String categoryName;

    private String productName;

    private BigDecimal sellingPrice;

    private BigDecimal mrp;

    private Integer stockQuantity;

    private ProductUnit unit;

    private String productImage;

    private ProductStatus status;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    private List<ProductSpecificationResponeDto> specifications;
    public ProductResponseDTO(
            Long id,
            Long categoryId,
            String categoryName,
            String productName,
            BigDecimal sellingPrice,
            BigDecimal mrp,
            Integer stockQuantity,
            ProductUnit unit,
            String productImage,
            ProductStatus status,
            String description,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.productName = productName;
        this.sellingPrice = sellingPrice;
        this.mrp = mrp;
        this.stockQuantity = stockQuantity;
        this.unit = unit;
        this.productImage = productImage;
        this.status = status;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}