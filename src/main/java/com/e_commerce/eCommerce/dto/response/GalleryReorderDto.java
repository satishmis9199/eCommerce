package com.e_commerce.eCommerce.dto.response;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class GalleryReorderDto {

    @NotEmpty(message = "orderedIds must not be empty")
    private List<Long> orderedIds;
}