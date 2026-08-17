package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {


    List<CartItem> findByCartIdAndUserId(Long id, Long id1);

    List<CartItem> findByCartId(Long id);


    int deleteByIdAndUserId(Long itemId, Long userId);

    Optional<CartItem> findByCartIdAndProductIdAndRowState(Long id, Long id1, int active);

    Optional<CartItem> findByCartIdAndProductIdAndRowStateAndTenantId(Long id, Long id1, int active, String tenantId);
}
