package com.e_commerce.eCommerce.dto;

import lombok.*;
import org.springframework.aot.generate.GeneratedTypeReference;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ReviewRequetDTO {

    private String productId;
    private Integer rating;
    private String text;

}
