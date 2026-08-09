package com.e_commerce.eCommerce.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategorySpecificationRequestDTO {

    private Long id;                 // Update ke time useful

    private String specificationName;

    private Boolean required;

    private Integer displayOrder;

}