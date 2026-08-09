package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.Order;
import com.e_commerce.eCommerce.entity.OrderStatus;
import com.e_commerce.eCommerce.entity.ReturnStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository< Order,Long> {
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

//    Order findByTenantIdAndId(Long orderId);
}
