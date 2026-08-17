package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.OrderStatus;
import com.e_commerce.eCommerce.entity.OrderTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderTrackingrepository extends JpaRepository<OrderTracking, Long> {
    List<OrderTracking> findByTenantIdAndOrderId(String tenantId, Long id);

    OrderTracking findByTenantIdAndOrderIdAndStatus(String tenantId, Long orderId, OrderStatus orderStatus);
}
