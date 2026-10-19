package com.e_commerce.eCommerce.dto.request;

import com.e_commerce.eCommerce.enums.MediaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GalleryRequestDto {

    @NotNull(message = "mediaType is required (IMAGE or VIDEO)")
    private MediaType mediaType;

    @NotBlank(message = "mediaUrl is required")
    @Size(max = 1000, message = "mediaUrl must be at most 1000 characters")
    private String mediaUrl;

    @Size(max = 1000, message = "thumbnailUrl must be at most 1000 characters")
    private String thumbnailUrl;

    @Size(max = 150, message = "title must be at most 150 characters")
    private String title;

    @Size(max = 500, message = "caption must be at most 500 characters")
    private String caption;

    @Size(max = 100, message = "altText must be at most 100 characters")
    private String altText;

    @Size(max = 60, message = "category must be at most 60 characters")
    private String category;
}