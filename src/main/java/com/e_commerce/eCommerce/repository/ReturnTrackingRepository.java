package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.ReturnTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReturnTrackingRepository extends JpaRepository<ReturnTracking, Long> {
    List<ReturnTracking> findByTenantIdAndOrderId(String tenantId, Long id);
}
