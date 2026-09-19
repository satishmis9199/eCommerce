package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.dto.response.FeatureSettingsDTO;
import com.e_commerce.eCommerce.entity.VendorFeatureSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorFeatureSettingsRepository
        extends JpaRepository<VendorFeatureSettings, Long> {

    @Query("""
        SELECT new com.e_commerce.eCommerce.dto.response.FeatureSettingsDTO(
            v.onlinePaymentEnabled,
            v.codEnabled,
            v.wishlistEnabled,
            v.reviewsEnabled
        )
        FROM VendorFeatureSettings v
        WHERE v.tenantId = :tenantId
    """)
    Optional<FeatureSettingsDTO> findFeatureSettingsByTenantId(
            @Param("tenantId") String tenantId
    );
    Optional<VendorFeatureSettings> findByTenantIdAndVendorId(String tenantId, Long vendorId);
}