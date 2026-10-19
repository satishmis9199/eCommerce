package com.e_commerce.eCommerce.mapper;

import com.e_commerce.eCommerce.dto.GalleryResponseDto;
import com.e_commerce.eCommerce.entity.GalleryItem;
import org.springframework.stereotype.Component;

@Component
public class GalleryMapper {

    public GalleryResponseDto toDto(GalleryItem g) {
        return GalleryResponseDto.builder()
                .id(g.getId())
                .mediaType(g.getMediaType())
                .mediaUrl(g.getMediaUrl())
                .thumbnailUrl(g.getThumbnailUrl())
                .title(g.getTitle())
                .caption(g.getCaption())
                .altText(g.getAltText())
                .category(g.getCategory())
                .sortOrder(g.getSortOrder())
                .active(g.isActive())
                .createdAt(g.getCreatedAt())
                .build();
    }
}