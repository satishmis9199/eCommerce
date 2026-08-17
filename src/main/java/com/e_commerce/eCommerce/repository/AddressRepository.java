package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserIdAndTenantIdAndIsDefaultAndRowState(Long id, String tenantId, boolean b, int i);

    long countByUserIdAndTenantIdAndRowState(Long id, String tenantId, int i);

    List<Address> findByUserIdAndTenantIdAndRowState(Long id, String tenantId, int i);

    Optional<Address> findByIdAndUserIdAndTenantIdAndRowState(Long addressId, Long id, String tenantId, int i);
}
