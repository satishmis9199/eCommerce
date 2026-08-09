package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.Address;
import com.e_commerce.eCommerce.entity.OrderAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderAddressRepository extends JpaRepository<OrderAddress,Long> {

    OrderAddress findByOrderIdAndTenantId(Long id, String tenantId);
}
