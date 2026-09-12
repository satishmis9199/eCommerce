package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.dto.request.VendorBrandingRequestDTO;
import com.e_commerce.eCommerce.dto.request.VendorBusinessAddressDTO;
import com.e_commerce.eCommerce.dto.request.VendorContactSocialRequestDTO;
import com.e_commerce.eCommerce.dto.response.VenodorBusinessProfile;
import com.e_commerce.eCommerce.entity.Vendor;
import com.e_commerce.eCommerce.entity.VendorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    boolean existsByEmail(String email);

    boolean existsByMobile(String phone);

    boolean existsByStoreName(String businessName);

    boolean existsBySubDomain(String subDomain);

    Optional<Vendor> findBySubDomain(String subDomain);

    Optional<Vendor> findByTenantId(String tenantId);

    List<Vendor> findByStatusNot(VendorStatus vendorStatus);


    Vendor findByTenantIdAndId(String tenantId, Long vendorid);


    @Query("""
        SELECT new com.e_commerce.eCommerce.dto.response.VenodorBusinessProfile(
            v.bussinessName,
            v.firstName,
            vb.gstNumber,
            vb.panNumber,
            vb.cinNumber,
            vb.businessDescription
        )
        FROM Vendor v
        JOIN VendorBusiness vb ON vb.vendor.id = v.id
        WHERE v.tenantId = :tenantId
        """)
    Optional<VenodorBusinessProfile> findVendorBusinessProfile(
            @Param("tenantId") String tenantId
    );



}
