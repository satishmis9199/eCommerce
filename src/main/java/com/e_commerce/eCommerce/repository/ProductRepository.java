package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.dto.ProductResponseDTO;
import com.e_commerce.eCommerce.entity.Product;
import com.e_commerce.eCommerce.entity.ProductStatus;
import jakarta.transaction.Transactional;
import org.apache.catalina.WebResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByTenantIdAndVendorId(String tenantId, Long vendorid);

    @Query("""
SELECT new com.e_commerce.eCommerce.dto.ProductResponseDTO(

p.id,
p.categoryId,
c.categoryName,
p.productName,
p.sellingPrice,
p.mrp,
p.stockQuantity,
p.unit,
p.productImage,
p.status,
p.description,
p.createdAt,
p.updatedAt

)

FROM Product p

JOIN ProductCategory c
ON p.categoryId = c.id

WHERE p.tenantId = :tenantId
AND p.vendorId = :vendorId

ORDER BY p.createdAt DESC
""")
    List<ProductResponseDTO> loadAllProducts(
            @Param("tenantId") String tenantId,
            @Param("vendorId") Long vendorId);

    Product findByIdAndTenantIdAndVendorId(Long id, String tenantId, Long vendorid);



    int countByCategoryIdAndTenantId(Long id, String tenantId);

    int countByCategoryIdAndTenantIdAndVendorId(Long categoryId, String tenant, Long vendorid);

    @Modifying
    @Transactional
    @Query("""
        UPDATE Product p
        SET p.categoryId = :newCategoryId
        WHERE p.categoryId = :oldCategoryId
        AND p.tenantId = :tenantId
        AND p.vendorId = :vendorId
    """)
    int moveProductsToCategory(
            @Param("newCategoryId") Long newCategoryId,
            @Param("oldCategoryId") Long oldCategoryId,
            @Param("tenantId") String tenantId,
            @Param("vendorId") Long vendorId);

    boolean existsByVendorIdAndCategoryIdAndProductNameIgnoreCase(Long vendorid, Long categoryId, String productName);

    Product findByIdAndTenantIdAndStatus(Long id, String tenantId, ProductStatus active);



    List<Product> findAllByTenantIdAndStatus(String tenant, ProductStatus productStatus);

    List<Product> findAllByTenantIdAndStatusAndFeatured(String tenant, ProductStatus productStatus, boolean b);

    List<Product> findAllByTenantIdAndStatusAndCategoryId(String tenantId, ProductStatus productStatus, Long categoryId);

    List<Product> findTop10ByTenantIdAndStatusOrderByCreatedAtDesc(String tenanId, ProductStatus productStatus);
    @Modifying
    @Query("""
    UPDATE Product p
    SET p.totalSold = COALESCE(p.totalSold, 0) + :quantity
    WHERE p.id = :productId
      AND p.tenantId = :tenantId
""")
    int incrementTotalSold(
            @Param("productId") Long productId,
            @Param("quantity") Long quantity,
            @Param("tenantId") String tenantId
    );

    List<Product> findTop10ByTenantIdAndStatusOrderByTotalSold(String tenanId, ProductStatus productStatus);

    List<Product> findTop4ByTenantIdAndVendorIdAndCategoryIdAndStatusAndIdNotOrderByTotalSoldDesc(String tenantId, Long id, Long categoryId, ProductStatus productStatus, Long id1);

    Product findByIdAndTenantId(Long productId, String tenantId);
}

