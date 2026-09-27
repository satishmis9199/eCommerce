package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.dto.ProductResponseDTO;
import com.e_commerce.eCommerce.entity.Product;
import com.e_commerce.eCommerce.entity.ProductStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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
        p.updatedAt,
        b.brandName
    )
    FROM Product p
    JOIN ProductCategory c
        ON p.categoryId = c.id
    LEFT JOIN Brands b
        ON p.brandId = b.id
    WHERE p.tenantId = :tenantId
      AND p.vendorId = :vendorId
    ORDER BY p.createdAt DESC
    """)
    List<ProductResponseDTO> loadAllProducts(
            @Param("tenantId") String tenantId,
            @Param("vendorId") Long vendorId
    );

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
    @Query(value = """
    SELECT p.*
    FROM products p
    JOIN vendors v
        ON v.id = p.vendor_id
    WHERE p.tenant_id = :tenant
      AND p.status = 'ACTIVE'
      AND p.featured = 1

      AND (
            :#{#categories == null || #categories.isEmpty()} = true
            OR p.category_id IN (:categories)
          )

      AND (
            :#{#brands == null || #brands.isEmpty()} = true
            OR v.store_name IN (:brands)
          )

      AND (
            :minPrice IS NULL
            OR p.selling_price >= :minPrice
          )

      AND (
            :maxPrice IS NULL
            OR p.selling_price <= :maxPrice
          )

    ORDER BY p.created_at DESC
    """, nativeQuery = true)
    List<Product> findFeaturedProductsWithFilter(
            @Param("tenant") String tenant,
            @Param("categories") List<Long> categories,
            @Param("brands") List<String> brands,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

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

    List<Product> findTop5ByTenantIdAndStatusAndProductNameContainingIgnoreCase(
            String tenantId, ProductStatus status, String productName);


    @Query("""
        SELECT p
        FROM Product p
        WHERE p.tenantId = :tenant
          AND p.status = :status

          AND (
                :categories IS NULL
                OR p.categoryId IN :categories
              )

          AND (
                :brands IS NULL
                OR EXISTS (
                    SELECT 1
                    FROM Vendor v
                    WHERE v.id = p.vendorId
                      AND v.storeName IN :brands
                )
              )

          AND (
                :minPrice IS NULL
                OR p.sellingPrice >= :minPrice
              )

          AND (
                :maxPrice IS NULL
                OR p.sellingPrice <= :maxPrice
              )

        ORDER BY p.createdAt DESC
        """)
    List<Product> findFilteredProducts(
            @Param("tenant") String tenant,
            @Param("status") ProductStatus status,
            @Param("categories") List<Long> categories,
            @Param("brands") List<String> brands,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );


    @Query("""
    SELECT COUNT(p)
    FROM Product p
    WHERE p.tenantId = :tenantId
      AND p.brandId = :id
""")
    long findCountOfBrandInTenant(
            @Param("id") Long id,
            @Param("tenantId") String tenantId
    );
    @Query("""
    SELECT COUNT(p)
    FROM Product p
    WHERE p.tenantId = :tenantId
""")
    Long totalProducts(
            @Param("tenantId") String tenantId
    );


    @Query("""
    SELECT COUNT(p)
    FROM Product p
    WHERE p.tenantId = :tenantId
      AND p.stockQuantity < 10
""")
    Long totalLowStockProducts(
            @Param("tenantId") String tenantId
    );
}

