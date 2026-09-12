package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.dto.request.VendorBusinessAddressDTO;
import com.e_commerce.eCommerce.entity.VendorAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorAddresss extends JpaRepository<VendorAddress, Long> {
    VendorAddress findByVendorId(Long vendorId);

    @Query("""
        SELECT new com.e_commerce.eCommerce.dto.request.VendorBusinessAddressDTO(
            v1.addressLine1,
            v1.addressLine2,
            v1.city,
            v1.state,
            v1.country,
            v1.postalCode
        )
        FROM VendorAddress v1
        WHERE v1.vendor.id = :vendorId
        """)
    Optional<VendorBusinessAddressDTO> findVendorBusiness(
            @Param("vendorId") Long vendorId
    );
}
