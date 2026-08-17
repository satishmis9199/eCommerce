package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.CategoryStatus;
import com.e_commerce.eCommerce.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<ProductCategory, Long> {
    boolean existsByTenantIdAndCategoryNameIgnoreCase(String tenantId, String categoryName);

    List<ProductCategory> findAllByTenantId(String tenantId);

    List<ProductCategory> findAllByTenantIdAndVendorId(String tenantId, Long vendorid);

    Optional<ProductCategory> findByIdAndVendorId(Long categoryId, Long vendorid);

    boolean existsByTenantIdAndCategoryNameIgnoreCaseAndIdNot(String tenantId, String categoryName, Long categoryId);


    ProductCategory findByIdAndVendorIdAndTenantId(Long id, Long vendorid, String tenant);

    List<ProductCategory> findAllByTenantIdAndStatus(String tenantId, CategoryStatus categoryStatus);


    ProductCategory findByIdAndTenantIdAndStatus(Long categoryId, String tenantId, CategoryStatus active);

    boolean existsByTenantIdAndIdAndStatus(String tenantId, Long categoryId, CategoryStatus categoryStatus);

    @Query("""
            SELECT c.id
            FROM ProductCategory c
            WHERE c.tenantId = :tenantId
            AND c.status = :status
            """)
    List<Long> findCategoryIdsByTenantIdAndVendorIdAndStatus(
            @Param("tenantId") String tenantId,
            @Param("status") CategoryStatus status
    );


    List<ProductCategory> findByTenantIdAndVendorIdAndStatus(String tenantId, Long aLong, CategoryStatus categoryStatus);
}
