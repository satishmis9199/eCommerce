package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.entity.Vendor;
import com.e_commerce.eCommerce.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final VendorRepository vendorRepository;

    /**
     * Cache
     * Key   = subdomain
     * Value = Vendor
     */
    private final Map<String, Vendor> tenantCache = new ConcurrentHashMap<>();


    /**
     * Resolve tenant from Host Name
     *
     * Example:
     * abc.mystore.com
     * xyz.mystore.com
     */
    public Vendor resolveTenant(String hostName) {

        // 1. Check Cache
        Vendor cachedVendor = tenantCache.get(hostName);

        if (cachedVendor != null) {
            return cachedVendor;
        }



        // 2. Fetch From Database
        System.out.println("hostname is {} "+hostName);
        Optional<Vendor> vendor = vendorRepository.findBySubDomain(hostName);
        if(vendor.isEmpty()){
            throw new RuntimeException("Vendor Not Present");
        }
        Vendor ven=vendor.get();


        // 3. Store In Cache
        tenantCache.put(hostName, ven);

        return ven;
    }


    /**
     * Find Vendor using tenantId
     */
    public Vendor getTenant(String tenantId) {

        return vendorRepository.findByTenantId(tenantId)
                .orElseThrow(() ->
                        new RuntimeException("Invalid Tenant"));
    }


    /**
     * Remove Single Tenant From Cache
     */
    public void evictTenant(String hostName) {

        tenantCache.remove(hostName);

    }


    /**
     * Clear Whole Cache
     */
    public void clearCache() {

        tenantCache.clear();

    }


    /**
     * Update Cache After Vendor Update
     */
    public void refreshTenant(Vendor vendor) {

        tenantCache.put(vendor.getSubDomain(), vendor);

    }

}