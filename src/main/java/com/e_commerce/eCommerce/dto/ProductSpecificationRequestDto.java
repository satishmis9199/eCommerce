package com.e_commerce.eCommerce.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ProductSpecificationRequestDto {
    private Long categorySpecificationId;
    private String value;
}
