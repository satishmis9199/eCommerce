package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.Vendor;
import com.e_commerce.eCommerce.entity.VendorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor,Long> {
    boolean existsByEmail(String email);

    boolean existsByMobile(String phone);

    boolean existsByStoreName(String businessName);

    boolean existsBySubDomain(String subDomain);
    Optional<Vendor> findBySubDomain(String subDomain);

    Optional<Vendor> findByTenantId(String tenantId);

    List<Vendor> findByStatusNot(VendorStatus vendorStatus);



    Vendor findByTenantIdAndId(String tenantId, Long vendorid);
}
