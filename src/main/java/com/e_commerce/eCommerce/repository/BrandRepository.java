package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.dto.response.BrandResponseDTO;
import com.e_commerce.eCommerce.entity.Brands;
import com.e_commerce.eCommerce.enums.BrandStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brands,Long> {
    @Query("""
                SELECT new com.e_commerce.eCommerce.dto.response.BrandResponseDTO(
                    b.id,
                    b.brandName,
                    b.brandDesc,
                    b.brandStatus
                )
                FROM Brands b
                WHERE b.tenantId = :tenantId
                ORDER BY b.id DESC
            """)
    List<BrandResponseDTO> getAllbrandByTenantId(
            @Param("tenantId") String tenantId
    );

    Brands findByTenantIdAndId(String tenantId, Long id);

    @Modifying
    @Transactional
    @Query("""
                DELETE FROM Brands b
                WHERE b.tenantId = :tenantId
                  AND b.id = :id
            """)
    void deleteAllByProduct(
            @Param("id") Long id,
            @Param("tenantId") String tenantId
    );

    Brands findByTenantIdAndIdAndBrandStatus(String tenantId, Long id, BrandStatus brandStatus);

    List<Brands> findAllByTenantId(String tenantId);
    @Query("""
                SELECT new com.e_commerce.eCommerce.dto.response.BrandResponseDTO(
                    b.id,
                    b.brandName,
                    b.brandDesc,
                    b.brandStatus
                )
                FROM Brands b
                WHERE b.tenantId = :tenantId
                AND brandStatus = :status
                ORDER BY b.id DESC
            """)
    List<BrandResponseDTO> getActiveBrandfortenant(@Param("tenantId") String tenantId,
                                                   @Param("status") BrandStatus status);
}
