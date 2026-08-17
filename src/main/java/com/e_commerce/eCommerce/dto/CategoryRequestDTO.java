package com.e_commerce.eCommerce.dto;

import com.e_commerce.eCommerce.entity.CategoryStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequestDTO {


    private String categoryName;

    private String description;

    private String imageUrl;
    private CategoryStatus status;
    private List<CategorySpecificationRequestDTO> specifications;
}
