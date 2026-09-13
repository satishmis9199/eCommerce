package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface WiShlistRepositorye extends JpaRepository<Wishlist,Long> {
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    boolean existsByUserIdAndProductIdAndTenantId(Long userId, Long productId, String tenantId);

    Wishlist findByUserIdAndProductIdAndTenantId(Long userId, Long productId, String tenantId);

    List<Wishlist> findAllByUserIdAndTenantIdOrderByCreatedAtDesc(Long userId, String tenantId);
}
