package com.e_commerce.eCommerce.dto;

import com.e_commerce.eCommerce.entity.ProductStatus;
import com.e_commerce.eCommerce.entity.ProductUnit;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDTO {

    @NotBlank(message = "Product Name is required.")
    private String productName;

    @NotNull(message = "Category is required.")
    private Long categoryId;

    @DecimalMin(value = "0.0", inclusive = false,
            message = "Selling Price must be greater than 0.")
    @NotNull(message = "Selling Price is required.")
    private BigDecimal sellingPrice;

    @DecimalMin(value = "0.0", inclusive = false,
            message = "MRP must be greater than 0.")
    private BigDecimal mrp;

    @NotNull(message = "Stock Quantity is required.")
    @Min(value = 0, message = "Stock Quantity cannot be negative.")
    private Integer stockQuantity;

    @NotNull(message = "Unit is required.")
    private ProductUnit unit;

    private String productImage;

    @NotNull(message = "Status is required.")
    private ProductStatus status;
    private Boolean featured;

    private String description;
    private List<ProductSpecificationRequestDto> specifications;

}