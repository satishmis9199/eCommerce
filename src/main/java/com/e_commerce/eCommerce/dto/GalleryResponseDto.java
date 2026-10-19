package com.e_commerce.eCommerce.dto;

import com.e_commerce.eCommerce.enums.MediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GalleryResponseDto {
    private Long id;
    private MediaType mediaType;
    private String mediaUrl;
    private String thumbnailUrl;
    private String title;
    private String caption;
    private String altText;
    private String category;
    private Integer sortOrder;
    private boolean active;
    private LocalDateTime createdAt;
}