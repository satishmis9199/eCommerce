package com.e_commerce.eCommerce.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GalleryStatusDto {

    @NotNull(message = "id is required")
    private Long id;

    @NotNull(message = "active is required")
    private Boolean active;
}