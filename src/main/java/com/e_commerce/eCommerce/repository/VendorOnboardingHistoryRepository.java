package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.VendorOnboardingHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VendorOnboardingHistoryRepository extends JpaRepository<VendorOnboardingHistory,Long> {
    List<VendorOnboardingHistory> findAllByOrderByCreatedAtDesc();

    List<VendorOnboardingHistory> findByVendorIdOrderByCreatedAtDesc(Long vendorId);

}
