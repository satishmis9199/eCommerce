package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.OrderAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderAddressRepository extends JpaRepository<OrderAddress, Long> {

    OrderAddress findByOrderIdAndTenantId(Long id, String tenantId);
}
