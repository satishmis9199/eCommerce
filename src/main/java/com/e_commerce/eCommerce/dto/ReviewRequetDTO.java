package com.e_commerce.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ReviewRequetDTO {

    private String productId;
    private Integer rating;
    private String text;

}
