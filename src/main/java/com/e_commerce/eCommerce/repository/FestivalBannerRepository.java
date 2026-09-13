package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.FestivalBanner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FestivalBannerRepository
        extends JpaRepository<FestivalBanner, Long> {


    // =========================================================
    // FIND BY ID + TENANT
    // =========================================================
    Optional<FestivalBanner> findByIdAndTenantId(
            Long id,
            String tenantId
    );


    // =========================================================
    // ALL TENANT BANNERS
    // =========================================================
    List<FestivalBanner> findAllByTenantIdOrderByCreatedAtDesc(
            String tenantId
    );


    // =========================================================
    // ACTIVE BANNER FOR CURRENT TIME
    // =========================================================
    @Query("""
        SELECT f
        FROM FestivalBanner f
        WHERE f.tenantId = :tenantId
          AND f.active = true
          AND (f.startAt IS NULL OR f.startAt <= :now)
          AND (f.endAt IS NULL OR f.endAt >= :now)
        ORDER BY f.createdAt DESC
        """)
    Optional<FestivalBanner> findActiveBannerForNow(
            @Param("tenantId") String tenantId,
            @Param("now") LocalDateTime now
    );


    // =========================================================
    // OVERLAPPING ACTIVE BANNER
    // =========================================================
    @Query("""
        SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END
        FROM FestivalBanner f
        WHERE f.tenantId = :tenantId
          AND f.active = true
          AND (:startAt IS NULL OR f.endAt IS NULL OR f.endAt >= :startAt)
          AND (:endAt IS NULL OR f.startAt IS NULL OR f.startAt <= :endAt)
        """)
    boolean existsOverlappingActiveBanner(
            @Param("tenantId") String tenantId,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt
    );


    // =========================================================
    // OVERLAP CHECK EXCLUDING CURRENT BANNER
    // =========================================================
    @Query("""
        SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END
        FROM FestivalBanner f
        WHERE f.tenantId = :tenantId
          AND f.active = true
          AND f.id <> :id
          AND (:startAt IS NULL OR f.endAt IS NULL OR f.endAt >= :startAt)
          AND (:endAt IS NULL OR f.startAt IS NULL OR f.startAt <= :endAt)
        """)
    boolean existsOverlappingActiveBannerExcludingId(
            @Param("tenantId") String tenantId,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt,
            @Param("id") Long id
    );

    boolean existsByTenantIdAndActiveTrue(String tenantId);
}