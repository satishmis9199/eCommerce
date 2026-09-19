
        package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.dto.SalesReportProjection;
import com.e_commerce.eCommerce.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long id);

    List<OrderItem> findAllByOrderIdAndTenantId(
            Long orderId,
            String tenantId
    );

    @Query(
            value = """
                    SELECT
                        o.order_number AS orderNumber,
                        o.created_at AS orderDate,

                        u.id AS customerId,
                        CONCAT(u.first_name, ' ', u.last_name) AS customerName,

                        oi.product_id AS productId,
                        oi.product_name AS productName,
                        oi.brand_name AS brandName,

                        oi.quantity AS quantity,
                        oi.mrp AS mrp,
                        oi.unit_price AS unitPrice,
                        oi.line_total AS lineTotal,

                        o.payment_method AS paymentMethod,
                        o.payment_status AS paymentStatus,
                        o.order_status AS orderStatus

                    FROM order_item oi

                    JOIN orders o
                        ON o.id = oi.order_id

                    JOIN users u
                        ON u.id = o.user_id

                    WHERE o.tenant_id = :tenantId
                      AND o.created_at >= :startDate
                      AND o.created_at <= :endDate
                      AND o.order_status NOT IN ('CANCELLED')

                    ORDER BY o.created_at DESC
                    """,
            nativeQuery = true
    )
    List<SalesReportProjection> getSalesReport(
            @Param("tenantId") String tenantId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    OrderItem findByOrderIdAndProductIdAndTenantId(
            Long orderId,
            Long productId,
            String tenantId
    );
}
