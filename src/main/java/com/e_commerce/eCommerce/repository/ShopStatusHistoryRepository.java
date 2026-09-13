package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.ShopStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShopStatusHistoryRepository
        extends JpaRepository<ShopStatusHistory, Long> {

    Optional<ShopStatusHistory> findFirstByTenantIdOrderByChangedAtDesc(
            String tenantId
    );
}