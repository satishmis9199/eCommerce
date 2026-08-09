package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.ProductSpecificationValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductSpecificationValueRepository extends JpaRepository<ProductSpecificationValue,Long> {
    ProductSpecificationValue findByProductIdAndCategorySpecificationIdAndTenantId(Long id, Long categorySpecificationId, String tenantId);

    List<ProductSpecificationValue> findByProductIdAndTenantId(Long id, String tenantId);
}
