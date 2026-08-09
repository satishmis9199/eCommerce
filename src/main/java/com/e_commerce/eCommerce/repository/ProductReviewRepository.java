package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.dto.ReviewResponseAdminDto;
import com.e_commerce.eCommerce.entity.ProductReview;
//import com.e_commerce.eCommerce.entity.ReviewStatus;
import com.e_commerce.eCommerce.entity.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview ,Long> {
    @Query("""
SELECT new com.e_commerce.eCommerce.dto.ReviewResponseAdminDto(
    r.id,
    p.id,
    p.productName,
    p.productImage,
    u.id,
    CONCAT(u.firstName,' ',u.lastName),
    u.email,
    r.rating,
    r.reviewTitle,
    r.reviewText,
    r.status,
    r.helpfulCount,
    r.reportCount,
    r.createdAt
)
FROM ProductReview r
JOIN Product p ON p.id = r.productId
JOIN User u ON u.id = r.userId
WHERE r.tenantId = :tenantId
  AND r.vendorId = :vendorId
  AND r.rowState = 1
  AND (:status IS NULL OR r.status = :status)
ORDER BY r.createdAt DESC
""")
    Page<ReviewResponseAdminDto> findReviews(
            @Param("tenantId") String tenantId,
            @Param("vendorId") Long vendorId,
            @Param("status") ReviewStatus status,
            Pageable pageable
    );

    ProductReview findByTenantIdAndUserIdAndId(String tenantId, Long id, Long id1);

    ProductReview findByIdAndTenantIdAndUserId(Long id, String tenantId, Long id1);

    ProductReview findByIdAndTenantId(Long id, String tenantId);

    List<ProductReview> findTop10ByTenantIdAndStatusOrderByCreatedAtDesc(String tenantId, ReviewStatus reviewStatus);
}
