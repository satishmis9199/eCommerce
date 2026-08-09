package com.e_commerce.eCommerce.dto;

import com.e_commerce.eCommerce.entity.CategoryStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.annotations.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCategoryDTO {


   private Long id;

    @NotNull
    private CategoryStatus status;

}