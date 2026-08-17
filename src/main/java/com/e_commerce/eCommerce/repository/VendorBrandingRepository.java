package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.VendorBranding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorBrandingRepository extends JpaRepository<VendorBranding, Long> {
    VendorBranding findByVendorId(Long vendorId);
}
