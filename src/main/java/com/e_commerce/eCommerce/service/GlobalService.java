
package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.entity.User;
import com.e_commerce.eCommerce.entity.Vendor;
import com.e_commerce.eCommerce.repository.UserRepos;
import com.e_commerce.eCommerce.repository.VendorRepository;
import org.springframework.stereotype.Service;

@Service
public class GlobalService {

    private final VendorRepository vendorRepository;
    private final UserRepos userRepos;

    public GlobalService(VendorRepository vendorRepository,
                         UserRepos userRepos) {
        this.vendorRepository = vendorRepository;
        this.userRepos = userRepos;
    }

    public Long validateVendors(String tenantId, Long userId) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new RuntimeException("Invalid Tenant");
        }
        if (userId == null) {
            throw new RuntimeException("Invalid User");
        }
        Vendor vendor = vendorRepository.findByTenantId(tenantId)
                .orElseThrow(() ->
                        new RuntimeException("Vendor Does not exist"));

        User user = userRepos.findByTenantIdAndId(tenantId, userId);

        if (user == null) {
            throw new RuntimeException("User Not found");
        }
        return vendor.getId();
    }
}

