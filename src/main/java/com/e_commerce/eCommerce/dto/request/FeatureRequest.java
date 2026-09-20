package com.e_commerce.eCommerce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeatureRequest {

    private String code;
    private String name;
    private String description;
    private Boolean active;
}