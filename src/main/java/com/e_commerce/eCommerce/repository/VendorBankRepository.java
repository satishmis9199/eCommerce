package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.Vendor;
import com.e_commerce.eCommerce.entity.VendorBank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VendorBankRepository extends JpaRepository<VendorBank, Long> {
    VendorBank findByVendorId(Long vendorId);

    Optional<VendorBank> findByVendor(Vendor vendor);
}
