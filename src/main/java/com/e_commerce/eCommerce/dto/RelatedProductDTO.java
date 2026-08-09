package com.e_commerce.eCommerce.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO for a single item inside the "Related Products" response.
 * Used by: GET /api/u1/v1/products/{productId}/related
 *
 * Only productId, name, image, price are currently rendered by the
 * frontend (renderQuickViewRelated()) — the rest are included so the
 * card can be upgraded later (rating, discount badge, stock dot, etc.)
 * without another backend change.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RelatedProductDTO {

    @JsonProperty("productId")
    private Long productId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("brand")
    private String brand;

    @JsonProperty("image")
    private String image;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("oldPrice")
    private BigDecimal oldPrice;

    @JsonProperty("discountPercent")
    private BigDecimal discountPercent;

    @JsonProperty("rating")
    private Double rating;

    @JsonProperty("reviewCount")
    private Integer reviewCount;

    @JsonProperty("stockLevel")
    private String stockLevel; // "in_stock" | "low_stock" | "out_of_stock"


}