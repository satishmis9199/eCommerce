package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.dto.UserBannerResponseDTo;
import com.e_commerce.eCommerce.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long> {
    List<Banner> findAllByTenantId(String tenantId);


    @Query("""
                SELECT new com.e_commerce.eCommerce.dto.UserBannerResponseDTo(
                    CAST(b.id AS string),
                    b.eyebrow,
                    b.title,
                    b.subtitle,
                    b.ctaLabel,
                    b.ctaLink,
                    b.imageUrl
                )
                FROM Banner b
                WHERE b.tenantId = :tenantId
                  AND b.vendorId = :vendorId
                  AND b.active = true
                  AND (b.startsAt IS NULL OR b.startsAt <= :now)
                  AND (b.endsAt IS NULL OR b.endsAt >= :now)
                ORDER BY b.displayOrder ASC
            """)
    List<UserBannerResponseDTo> findActiveBanners(
            @Param("tenantId") String tenantId,
            @Param("vendorId") Long vendorId,
            @Param("now") LocalDateTime now
    );

    Banner findByIdAndTenantId(Long id, String tenantId);
}
