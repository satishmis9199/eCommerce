package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.dto.GalleryResponseDto;
import com.e_commerce.eCommerce.entity.GalleryItem;
import com.e_commerce.eCommerce.enums.MediaType;
import com.e_commerce.eCommerce.mapper.GalleryMapper;
import com.e_commerce.eCommerce.repository.GalleryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PublicGalleryService {

    private final GalleryRepository repo;
    private final GalleryMapper mapper;

    @Transactional(readOnly = true)
    public Page<GalleryResponseDto> page(String tenantId, MediaType type, String category, int page, int size) {
        String cat = (category == null || category.isBlank()) ? null : category.trim();
        PageRequest pageable = PageRequest.of(Math.max(0, page), Math.max(1, Math.min(size, 50)));
        return repo.findPublic(tenantId, type, cat, pageable).map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public GalleryResponseDto byId(String tenantId, Long id) {
        GalleryItem item = repo.findByIdAndTenantIdAndDeletedFalse(id, tenantId)
                .filter(GalleryItem::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gallery item not found"));
        return mapper.toDto(item);
    }
}