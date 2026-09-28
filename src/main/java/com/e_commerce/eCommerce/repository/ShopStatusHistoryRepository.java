package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.dto.response.ShopHistoryResponse;
import com.e_commerce.eCommerce.entity.ShopStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShopStatusHistoryRepository
        extends JpaRepository<ShopStatusHistory, Long> {

    Optional<ShopStatusHistory> findFirstByTenantIdOrderByChangedAtDesc(
            String tenantId
    );

    @Query("""
        SELECT new com.e_commerce.eCommerce.dto.response.ShopHistoryResponse(
            h.id,
            h.status,
            h.reason,
            h.changedAt,
            h.changedBy,
            CONCAT(
                COALESCE(u.firstName, ''),
                ' ',
                COALESCE(u.lastName, '')
            )
        )
        FROM ShopStatusHistory h
        LEFT JOIN User u ON u.id = h.changedBy
        WHERE h.tenantId = :tenantId
        ORDER BY h.changedAt DESC
        """)
    List<ShopHistoryResponse> findAllWithTenant(
            @Param("tenantId") String tenantId
    );
}