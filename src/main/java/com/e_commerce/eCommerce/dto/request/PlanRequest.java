package com.e_commerce.eCommerce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanRequest {

    private String code;
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean active;
}