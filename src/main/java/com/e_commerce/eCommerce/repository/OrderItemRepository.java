package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository< OrderItem,Long> {
    List<OrderItem> findByOrderId(Long id);

    List<OrderItem> findAllByOrderIdAndTenantId(Long orderId, String tenantid);


    OrderItem findByOrderIdAndProductIdAndTenantId(Long id, Long pid, String tenantId);
}
