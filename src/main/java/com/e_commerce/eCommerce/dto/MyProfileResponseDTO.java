package com.e_commerce.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyProfileResponseDTO {

    private boolean success;
    private String message;
    private MyProfileDTO profile;
}