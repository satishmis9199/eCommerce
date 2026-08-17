package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.VendorBank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorBankRepository extends JpaRepository<VendorBank, Long> {
    VendorBank findByVendorId(Long vendorId);
}
