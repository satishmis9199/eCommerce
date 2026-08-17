package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.VendorPolicy;
import com.e_commerce.eCommerce.enums.PolicyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorPolicyRepository extends JpaRepository<VendorPolicy, Long> {

    Optional<VendorPolicy> findByTenantIdAndVendorIdAndPolicyType(
            String tenantId, Long vendorId, PolicyType policyType);

    List<VendorPolicy> findByTenantIdAndVendorId(String tenantId, Long vendorId);

    void deleteByTenantIdAndVendorIdAndPolicyType(
            String tenantId, Long vendorId, PolicyType policyType);
}