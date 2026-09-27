package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.Order;
import com.e_commerce.eCommerce.entity.OrderStatus;
import com.e_commerce.eCommerce.entity.ReturnStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByTenantIdAndId(String tenantId, String orderId);

    Order findByTenantIdAndOrderNumber(String tenantId, String orderId);

    List<Order> findByTenantIdAndUserIdAndOrderStatus(String tenantId, Long id, OrderStatus orderStatus);

    List<Order> findAllByTenantIdAndUserId(String tenantId, Long id);

    Order findByidAndTenantIdAndVendorId(Long orderId, String tenantid, Long id);

    Order findByOrderNumberAndTenantIdAndVendorId(String orderId, String tenantId, Long id);

//    Order findByOrderNumberAndTenantIdAndVendorIdAndUserId(String orderIds, String tenantId, Long id, Long id1);

    List<Order> findAllByTenantId(String tenantId);

    List<Order> findByTenantId(String tenantId);

    Order findByOrderNumberAndTenantIdAndVendorIdAndUserId(String orderIds, String tenantId, Long id, Long id1);

    Order findByidAndTenantId(Long orderId, String tenantId);

    List<Order> findAllByTenantIdAndReturnStatus(String tenantId, ReturnStatus returnStatus);

    Order findByTenantIdAndOrderNumberAndUserId(String tenantId, String orderId, Long id);

    Optional<Order> findTopByTenantIdOrderByCreatedAtDesc(String tenantId);

    @Query("""
    SELECT COALESCE(SUM(o.subtotal), 0)
    FROM Order o
    WHERE o.tenantId = :tenantId
      AND o.orderStatus <> com.e_commerce.eCommerce.entity.OrderStatus.CANCELLED
""")
    BigDecimal findTotalCountOfRevenue(
            @Param("tenantId") String tenantId
    );



    @Query("""
    SELECT COALESCE(SUM(o.subtotal), 0)
    FROM Order o
    WHERE o.tenantId = :tenantId
      AND o.createdAt >= :startDate
      AND o.createdAt <= :endDate
      AND o.orderStatus <> com.e_commerce.eCommerce.entity.OrderStatus.CANCELLED
""")
    BigDecimal findRevenueBetween(
            @Param("tenantId") String tenantId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );




    @Query("""
    SELECT COUNT(o)
    FROM Order o
    WHERE o.tenantId = :tenantId
      AND o.orderStatus <> com.e_commerce.eCommerce.entity.OrderStatus.CANCELLED
""")
    Long totalOrders(
            @Param("tenantId") String tenantId

    );
    @Query("""
    SELECT COUNT(o)
    FROM Order o
    WHERE o.tenantId = :tenantId
      AND o.createdAt >= :startDate
      AND o.createdAt <= :endDate
      AND o.orderStatus <> com.e_commerce.eCommerce.entity.OrderStatus.CANCELLED
""")
    Long totalOrdersBetween(
            @Param("tenantId") String tenantId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


    @Query("""
    SELECT COUNT(o)
    FROM Order o
    WHERE o.tenantId = :tenantId
      AND o.orderStatus = com.e_commerce.eCommerce.entity.OrderStatus.PLACED
""")
    Long pendingOrders(
            @Param("tenantId") String tenantId
    );
}

