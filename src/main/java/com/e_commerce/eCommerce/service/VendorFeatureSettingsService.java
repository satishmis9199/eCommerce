package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.dto.request.VendorFeatureSettingsRequest;
import com.e_commerce.eCommerce.dto.response.VendorFeatureSettingsResponse;
import com.e_commerce.eCommerce.entity.Vendor;
import com.e_commerce.eCommerce.entity.VendorFeatureSettings;
import com.e_commerce.eCommerce.repository.VendorFeatureSettingsRepository;
import com.e_commerce.eCommerce.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VendorFeatureSettingsService {

    private final VendorFeatureSettingsRepository repository;
    private final VendorRepository vendorRepository;
    public VendorFeatureSettings createDefaultsIfAbsent(String tenantId, Long vendorId) {
        return repository.findByTenantIdAndVendorId(tenantId, vendorId)
                .orElseGet(() -> insertDefaults(tenantId, vendorId));
    }

    public VendorFeatureSettingsResponse get(String tenantId, Long vendorId
    ) {
        return toResponse(createDefaultsIfAbsent(tenantId, vendorId));
    }

    public VendorFeatureSettingsResponse update(String tenantId, Long vendorId,
                                                VendorFeatureSettingsRequest request) {
        VendorFeatureSettings settings = createDefaultsIfAbsent(tenantId, vendorId);

        if (request.getOnlinePaymentEnabled() != null) settings.setOnlinePaymentEnabled(request.getOnlinePaymentEnabled());
        if (request.getCodEnabled() != null)           settings.setCodEnabled(request.getCodEnabled());
        if (request.getWishlistEnabled() != null)      settings.setWishlistEnabled(request.getWishlistEnabled());
        if (request.getReviewsEnabled() != null)       settings.setReviewsEnabled(request.getReviewsEnabled());
        if (request.getChatEnabled() != null)          settings.setChatEnabled(request.getChatEnabled());
        if (request.getCouponsEnabled() != null)       settings.setCouponsEnabled(request.getCouponsEnabled());

        // With both payment methods off nobody could check out.
        if (!settings.isOnlinePaymentEnabled() && !settings.isCodEnabled()) {
            throw new IllegalArgumentException(
                    "At least one payment method (Online Payment or Cash on Delivery) must stay enabled.");
        }

        return toResponse(repository.save(settings));
    }

    private VendorFeatureSettings insertDefaults(String tenantId, Long vendorId) {
        try {
            return repository.saveAndFlush(
                    VendorFeatureSettings.builder()
                            .tenantId(tenantId)
                            .vendorId(vendorId)
                            .build());
        } catch (DataIntegrityViolationException e) {/*fea*/
            log.debug("Feature settings already created for tenant={}, vendor={}", tenantId, vendorId);
            return repository.findByTenantIdAndVendorId(tenantId, vendorId).orElseThrow(() -> e);
        }
    }

    private VendorFeatureSettingsResponse toResponse(VendorFeatureSettings s) {
        return VendorFeatureSettingsResponse.builder()
                .onlinePaymentEnabled(s.isOnlinePaymentEnabled())
                .codEnabled(s.isCodEnabled())
                .wishlistEnabled(s.isWishlistEnabled())
                .reviewsEnabled(s.isReviewsEnabled())
                .chatEnabled(s.isChatEnabled())
                .couponsEnabled(s.isCouponsEnabled())
                .updatedAt(s.getUpdatedAt())
                .build();
    }

    public Long findVendorId(String tenantId) {
        Optional<Vendor> vendor=vendorRepository.findByTenantId(tenantId);
        return vendor.get().getId();
    }
}