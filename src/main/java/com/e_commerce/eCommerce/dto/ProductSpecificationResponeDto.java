package com.e_commerce.eCommerce.dto;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class ProductSpecificationResponeDto {
    private Long categorySpecificationId;
    private String specificationName;
    private String value;


}
