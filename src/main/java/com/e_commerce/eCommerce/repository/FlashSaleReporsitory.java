package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.FlashSale;
import com.e_commerce.eCommerce.entity.FlashSaleStatus;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlashSaleReporsitory extends JpaRepository<FlashSale,Long> {
    List<FlashSale> findAllByTenantIdAndVendorId(String tenantId, Long id);


    FlashSale findByTenantIdAndVendorIdAndId(String tenantId, Long id, Long flashSaleId);

    FlashSale findByTenantIdAndStatus(String tenantId, FlashSaleStatus flashSaleStatus);
    @Query("""
SELECT f
FROM FlashSale f
WHERE f.tenantId = :tenantId
AND f.vendorId = :vendorId
AND f.status = :status
AND (
        :startDateTime <= f.endDateTime
    AND :endDateTime >= f.startDateTime
)
""")
    List<FlashSale> findConflictingFlashSales(
            String tenantId,
            Long vendorId,

            LocalDateTime startDateTime,
            LocalDateTime endDateTime,  FlashSaleStatus status
    );


    @Query("""
SELECT f
FROM FlashSale f
WHERE f.tenantId = :tenantId
AND f.vendorId = :vendorId
AND f.status = :status
AND f.id <> :flashSaleId
AND (
        :startDateTime <= f.endDateTime
    AND :endDateTime >= f.startDateTime
)
""")
    List<FlashSale> findConflictingFlashSalesForUpdate(
            @Param("tenantId") String tenantId,
            @Param("vendorId") Long vendorId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("status") FlashSaleStatus status,
            @Param("flashSaleId") Long flashSaleId
    );

    FlashSale findByTenantIdAndStatusAndStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqual(String tenantId, FlashSaleStatus flashSaleStatus, LocalDateTime now, LocalDateTime now1);
}
