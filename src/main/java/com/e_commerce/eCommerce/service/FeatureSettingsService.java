package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.dto.response.FeatureSettingsDTO;
import com.e_commerce.eCommerce.repository.VendorFeatureSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeatureSettingsService {

    private final VendorFeatureSettingsRepository featureSettingsRepository;

    public FeatureSettingsDTO getFeatureDetails(String tenantId) {

        if (tenantId == null || tenantId.isBlank()) {
            throw new RuntimeException("Invalid tenant");
        }

        return featureSettingsRepository
                .findFeatureSettingsByTenantId(tenantId)
                .orElseThrow(() ->
                        new RuntimeException("Feature settings not found for tenant: " + tenantId)
                );
    }
}