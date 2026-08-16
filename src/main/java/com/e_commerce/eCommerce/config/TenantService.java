package com.e_commerce.eCommerce.config;

import com.e_commerce.eCommerce.entity.Vendor;
import com.e_commerce.eCommerce.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantService {

    private final VendorRepository vendorRepository;

    @Cacheable(value = "tenants", key = "#hostName")
    public Vendor resolveTenant(String hostName) {
        return vendorRepository.findBySubDomain(hostName)
                .orElseThrow(() ->
                        new RuntimeException("Vendor Not Present"));
    }

    public Vendor getTenant(String tenantId) {

        return vendorRepository.findByTenantId(tenantId)
                .orElseThrow(() ->
                        new RuntimeException("Invalid Tenant"));
    }

    @CacheEvict(value = "tenants", key = "#hostName")
    public void evictTenant(String hostName) {
        log.error("Tenant cache evicted: " + hostName);
    }

    @CacheEvict(value = "tenants", allEntries = true)
    public void clearCache() {
        log.error("All tenant cache cleared");
    }

    @CachePut(value = "tenants", key = "#vendor.subDomain")
    public Vendor refreshTenant(Vendor vendor) {

        log.error("Tenant cache refreshed: "
                + vendor.getSubDomain());

        return vendor;
    }
}