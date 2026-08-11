package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.Notice;
import com.e_commerce.eCommerce.enums.NoticeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice,Long> {
    List<Notice> findByTenantId(String tenantId);
    @Query("""
        SELECT n FROM Notice n
        WHERE n.tenantId = :tenantId
          AND n.vendorId = :vendorId
          AND n.status = :status
          AND (n.startAt IS NULL OR n.startAt <= :now)
          AND (n.endAt IS NULL OR n.endAt >= :now)
        ORDER BY n.priority DESC, n.createdAt DESC
        """)
    List<Notice> findActiveNotices(
            @Param("tenantId") String tenantId,
            @Param("vendorId") Long vendorId,
            @Param("status") NoticeStatus status,
            @Param("now") LocalDateTime now
    );

    Optional<Notice> findByIdAndTenantId(Long noticeId, String tenantId);
}
