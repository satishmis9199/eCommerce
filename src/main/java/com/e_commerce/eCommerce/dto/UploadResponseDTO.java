package com.e_commerce.eCommerce.dto;

import lombok.Data;

@Data
public class UploadResponseDTO {

    private boolean success;

    private String message;

    private String objectKey;

}