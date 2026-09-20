package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.VendorRequestTracking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface VendorRequestTrackingRepository extends JpaRepository<VendorRequestTracking,Long> {
    List<VendorRequestTracking> findByVendorRequestIdOrderByCreatedAtAsc(
            Long vendorRequestId
    );
}
