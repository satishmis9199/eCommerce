package com.e_commerce.eCommerce.dto.response;

import com.e_commerce.eCommerce.enums.BrandStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandResponseDTO {

    private Long id;
    private String brandName;
    private String description;
    private BrandStatus status;
}