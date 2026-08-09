package com.e_commerce.eCommerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTo {

    private Long productId;
    private String name;
    private String image;
    private BigDecimal price;

    private boolean review;

    public Long getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @JsonProperty("isReview")
    public boolean isReview() {
        return review;
    }
}