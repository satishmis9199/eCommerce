package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.VendorAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorAddresss extends JpaRepository<VendorAddress, Long> {
    VendorAddress findByVendorId(Long vendorId);
}
