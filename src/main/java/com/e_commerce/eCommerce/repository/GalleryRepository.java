package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.GalleryItem;
import com.e_commerce.eCommerce.enums.MediaType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GalleryRepository extends JpaRepository<GalleryItem, Long> {

    List<GalleryItem> findByTenantIdAndDeletedFalseOrderBySortOrderAscIdDesc(String tenantId);

    Optional<GalleryItem> findByIdAndTenantIdAndDeletedFalse(Long id, String tenantId);

    List<GalleryItem> findByTenantIdAndDeletedFalseAndIdIn(String tenantId, Collection<Long> ids);

    @Query("select coalesce(max(g.sortOrder), 0) from GalleryItem g where g.tenantId = :tenantId")
    int maxSortOrder(@Param("tenantId") String tenantId);

    @Query("""
        select g from GalleryItem g
        where g.tenantId = :tenantId and g.active = true and g.deleted = false
          and (:type is null or g.mediaType = :type)
          and (:category is null or g.category = :category)
        order by g.sortOrder asc, g.id desc
        """)
    Page<GalleryItem> findPublic(@Param("tenantId") String tenantId,
                                 @Param("type") MediaType type,
                                 @Param("category") String category,
                                 Pageable pageable);
}