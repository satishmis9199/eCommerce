package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.Vendor;
import com.e_commerce.eCommerce.entity.VendorBusiness;
import org.springframework.data.jpa.repository.JpaRepository;

public interface vendorBussinesss extends JpaRepository<VendorBusiness,Long> {
    VendorBusiness findByVendorId(Long vendorId);

    VendorBusiness findByVendorIdAndTenantId(Vendor vendor, String tenantId);
}
