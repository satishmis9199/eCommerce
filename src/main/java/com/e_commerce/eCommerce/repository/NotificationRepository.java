package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {
    Page<Notification> findByTenantIdOrderByCreatedAtDesc(
            String tenantId,
            Pageable pageable
    );
    long countByTenantIdAndIsReadFalse(
            String tenantId
    );
    @Modifying
    @Query("""
            UPDATE Notification n
            SET n.isRead = true
            WHERE n.tenantId = :tenantId
              AND n.isRead = false
            """)
    int markAllAsReadForTenant(
            @Param("tenantId") String tenantId
    );
    boolean existsByIdAndTenantId(
            Long id,
            String tenantId
    );
}