package com.e_commerce.eCommerce.dto;

import com.e_commerce.eCommerce.entity.CategoryStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponseDTO {

    private Long id;

    private String categoryName;

    private String description;
    private int productCount;

    private String imageUrl;

    private CategoryStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    private List<CategorySpecificationRequestDTO> specifications;


}