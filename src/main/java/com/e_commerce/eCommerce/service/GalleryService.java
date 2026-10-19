package com.e_commerce.eCommerce.service;


import com.e_commerce.eCommerce.dto.GalleryResponseDto;
import com.e_commerce.eCommerce.dto.request.GalleryRequestDto;
import com.e_commerce.eCommerce.entity.GalleryItem;
import com.e_commerce.eCommerce.enums.MediaType;
import com.e_commerce.eCommerce.repository.GalleryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GalleryService {

    private final GalleryRepository repo;

    /* ===================== VENDOR ===================== */

    @Transactional(readOnly = true)
    public List<GalleryResponseDto> listForVendor(String tenantId) {
        return repo.findByTenantIdAndDeletedFalseOrderBySortOrderAscIdDesc(tenantId)
                .stream().map(this::toDto).toList();
    }

    @Transactional
    public GalleryResponseDto create(String tenantId, Long vendorId, GalleryRequestDto dto) {
        validate(dto);
        GalleryItem item = GalleryItem.builder()
                .tenantId(tenantId)
                .vendorId(vendorId)
                .sortOrder(repo.maxSortOrder(tenantId) + 1)
                .build();
        apply(item, dto);
        GalleryItem saved = repo.save(item);
        log.info("Gallery item {} created (tenant {})", saved.getId(), tenantId);
        return toDto(saved);
    }

    @Transactional
    public GalleryResponseDto update(String tenantId, Long id, GalleryRequestDto dto) {
        validate(dto);
        GalleryItem item = find(tenantId, id);
        apply(item, dto);
        return toDto(item);
    }

    @Transactional
    public void setActive(String tenantId, Long id, boolean active) {
        find(tenantId, id).setActive(active);
    }

    @Transactional
    public void reorder(String tenantId, List<Long> orderedIds) {
        if (new HashSet<>(orderedIds).size() != orderedIds.size()) {
            throw bad("Duplicate ids in orderedIds");
        }
        Map<Long, GalleryItem> byId = repo.findByTenantIdAndDeletedFalseAndIdIn(tenantId, orderedIds)
                .stream().collect(Collectors.toMap(GalleryItem::getId, Function.identity()));
        if (byId.size() != orderedIds.size()) {
            throw bad("Some items were not found");
        }
        int order = 1;
        for (Long id : orderedIds) {
            byId.get(id).setSortOrder(order++);
        }
    }

    @Transactional
    public void delete(String tenantId, Long id) {
        find(tenantId, id).setDeleted(true);      // soft delete
    }

    /* ===================== PUBLIC (storefront) ===================== */

    @Transactional(readOnly = true)
    public Page<GalleryResponseDto> getPublic(String tenantId, MediaType type, String category, int page, int size) {
        String cat = (category == null || category.isBlank()) ? null : category.trim();
        PageRequest pageable = PageRequest.of(Math.max(0, page), Math.max(1, Math.min(size, 50)));
        return repo.findPublic(tenantId, type, cat, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public GalleryResponseDto getOne(String tenantId, Long id) {
        GalleryItem item = find(tenantId, id);
        if (!item.isActive()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Gallery item not found");
        }
        return toDto(item);
    }

    /* ===================== helpers ===================== */

    private GalleryItem find(String tenantId, Long id) {
        return repo.findByIdAndTenantIdAndDeletedFalse(id, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gallery item not found"));
    }

    private void apply(GalleryItem item, GalleryRequestDto d) {
        item.setMediaType(d.getMediaType());
        item.setMediaUrl(d.getMediaUrl().trim());
        item.setThumbnailUrl(blankToNull(d.getThumbnailUrl()));
        item.setTitle(blankToNull(d.getTitle()));
        item.setCaption(blankToNull(d.getCaption()));
        item.setAltText(blankToNull(d.getAltText()));
        item.setCategory(blankToNull(d.getCategory()));
    }

    private void validate(GalleryRequestDto d) {
        if (!isSafeRef(d.getMediaUrl().trim())) {
            throw bad("mediaUrl must be an https link or an uploaded file path");
        }
        String thumb = blankToNull(d.getThumbnailUrl());
        if (thumb != null && !isSafeRef(thumb)) {
            throw bad("thumbnailUrl must be an https link or an uploaded file path");
        }
    }

    // https link ya R2 ka relative key (jaise "gallery/abc/video.mp4"). javascript:, data:, http: sab reject.
    private boolean isSafeRef(String v) {
        if (v.regionMatches(true, 0, "https://", 0, 8)) {
            try {
                URI u = new URI(v);
                return u.getHost() != null;
            } catch (URISyntaxException e) {
                return false;
            }
        }
        return !(v.contains(":") || v.contains("..") || v.startsWith("/") || v.startsWith("\\"));
    }

    private String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    private ResponseStatusException bad(String msg) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }

    private GalleryResponseDto toDto(GalleryItem g) {
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