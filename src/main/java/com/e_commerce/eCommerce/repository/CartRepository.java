package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {


    Cart findByTenantIdAndVendorIdAndUserIdAndRowState(String tenant, Long id, Long id1, int rowstate);


    Cart findByVendorIdAndUserIdAndTenantId(Long id, Long id1, String tenantId);

    Optional<Cart> findByTenantIdAndVendorIdAndUserId(String tenantId, Long vendorId, Long userId);
}
