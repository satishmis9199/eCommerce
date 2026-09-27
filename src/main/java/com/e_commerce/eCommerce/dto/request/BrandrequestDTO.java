package com.e_commerce.eCommerce.dto.request;

import com.e_commerce.eCommerce.enums.BrandStatus;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class BrandrequestDTO {

    private String brandName;
    private String description;
    private BrandStatus brandStatus;
}
