package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.dto.request.VendorBrandingRequestDTO;
import com.e_commerce.eCommerce.dto.request.VendorContactSocialRequestDTO;
import com.e_commerce.eCommerce.entity.VendorBranding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VendorBrandingRepository extends JpaRepository<VendorBranding, Long> {
    VendorBranding findByVendorId(Long vendorId);

    @Query("""
        SELECT new com.e_commerce.eCommerce.dto.request.VendorBrandingRequestDTO(
        v1.primaryColor,
        v1.secondaryColor,
        v1.logoUrl,
        v1.bannerUrl,
        v1.faviconUrl
        
        ) FROM VendorBranding v1 where v1.vendor.id =:id
        """)
    Optional<VendorBrandingRequestDTO> loaddBrandingDetail(@Param("id") Long id);

    @Query("""
        SELECT new com.e_commerce.eCommerce.dto.request.VendorContactSocialRequestDTO(
        s1.supportEmail,
        s1.supportPhone,
        s1.whatsApp,
        s1.website,
        s1.facebookUrl,
        s1.instagramUrl,
        s1.linkedinUrl,
        s1.youtubeUrl
        ) from VendorBranding s1 WHERE s1.vendor.id =:id
        """)
    VendorContactSocialRequestDTO loadVendorContactInfo(@Param("id") Long id);
}
