package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.Ad;
import com.e_commerce.eCommerce.enums.AdSlotKey;
import com.e_commerce.eCommerce.enums.AdStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AdRepository extends JpaRepository<Ad, Long> {
    List<Ad> findByTenantIdOrderByCreatedAtDesc(String tenantId);
    List<Ad> findByStatusOrderByCreatedAtAsc(AdStatus status);
    @Query("""
        SELECT a FROM Ad a
        WHERE a.status = com.e_commerce.eCommerce.enums.AdStatus.APPROVED
          AND (a.expiresAt IS NULL OR a.expiresAt >= :now)
    """)
    List<Ad> findAllServable(@Param("now") LocalDateTime now);
    @Query("""
        SELECT a FROM Ad a
        WHERE a.slotKey = :slotKey
          AND a.status = com.e_commerce.eCommerce.enums.AdStatus.APPROVED
          AND (a.expiresAt IS NULL OR a.expiresAt >= :now)
        ORDER BY a.priority DESC, a.impressions ASC, a.id ASC
    """)
    List<Ad> findServableBySlot(@Param("slotKey") AdSlotKey slotKey,
                                @Param("now") LocalDateTime now,
                                Pageable pageable);

    List<Ad> findBySlotKeyOrderByPriorityDesc(AdSlotKey slotKey);
    List<Ad> findByTenantIdAndSlotKeyOrderByCreatedAtDesc(String tenantId, AdSlotKey slotKey);
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Ad a SET a.impressions = a.impressions + 1 WHERE a.id = :id")
    int incrementImpressions(@Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Ad a SET a.clicks = a.clicks + 1 WHERE a.id = :id")
    int incrementClicks(@Param("id") Long id);

    long countBySlotKeyAndStatus(AdSlotKey slotKey, AdStatus status);

    Optional<Ad> findByIdAndTenantId(Long id, String tenantId);
}