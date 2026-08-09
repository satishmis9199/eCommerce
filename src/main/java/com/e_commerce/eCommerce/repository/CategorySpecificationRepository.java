package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.CategorySpecification;
import com.e_commerce.eCommerce.entity.CategoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;



import java.util.List;

public interface CategorySpecificationRepository extends JpaRepository<CategorySpecification,Long> {
    List<CategorySpecification> findByCategoryIdAndTenantId(Long id, String tenantId);
    CategorySpecification findByIdAndCategoryIdAndTenantId(Long id,Long cid,String tenant);

    List<CategorySpecification> findByCategoryIdAndVendorIdAndTenantIdAndStatusOrderByDisplayOrderAsc(Long id, Long id1, String tenantId, CategoryStatus categoryStatus);
}
