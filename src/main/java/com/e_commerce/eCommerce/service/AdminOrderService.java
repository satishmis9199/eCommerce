package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.OrderUpdatRequestDTO;
import com.e_commerce.eCommerce.entity.*;
import com.e_commerce.eCommerce.event.OrderDeliveredEvent;
import com.e_commerce.eCommerce.repository.OrderRepository;
import com.e_commerce.eCommerce.repository.OrderTrackingrepository;
import com.e_commerce.eCommerce.repository.VendorRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@AllArgsConstructor
public class AdminOrderService {
    private final OrderService orderService;
    private VendorRepository vendorRepository;
    private OrderRepository orderRepository;
    private OrderTrackingrepository orderTrackingrepository;
    private final PdfInvoiceService pdfInvoiceServicel;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public String updateStatus(CustomUserDetail userDetail, OrderUpdatRequestDTO orderUpdatRequestDTO) {

        log.info("Updating order status. Order Number : {}", orderUpdatRequestDTO.getOrderId());

        String tenantId = TenantContext.getTenantId();

        if (tenantId == null) {
            throw new RuntimeException("Invalid Tenant Id.");
        }

        if (userDetail == null || userDetail.getRole() != Roles.ADMIN) {
            throw new RuntimeException("Unauthorized Access.");
        }

        Vendor vendor = vendorRepository.findByTenantId(tenantId)
                .orElseThrow(() -> {
                    return new RuntimeException("Vendor does not exist.");
                });

        Order order = orderRepository.findByTenantIdAndOrderNumber(
                tenantId,
                orderUpdatRequestDTO.getOrderId());

        if (order == null) {
            throw new RuntimeException("Order does not exist.");
        }

        OrderStatus previousStatus = order.getOrderStatus();
        OrderStatus newStatus = OrderStatus.valueOf(orderUpdatRequestDTO.getStatus().toUpperCase());
        if (previousStatus == newStatus) {
            throw new RuntimeException("Order is already in " + previousStatus + " status.");
        }


        if (newStatus.getSequence() <= previousStatus.getSequence()) {
            throw new RuntimeException(
                    "Order cannot move back from "
                            + previousStatus + " to " + newStatus
            );
        }

        order.setUpdatedAt(LocalDateTime.now());
        order.setOrderStatus(newStatus);


        OrderTracking orderTracking = OrderTracking.builder()
                .tenantId(tenantId)
                .vendorId(vendor.getId())
                .orderId(order.getId())
                .previousStatus(previousStatus)
                .status(newStatus)
                .remarks(orderUpdatRequestDTO.getRemarks())
                .location(orderUpdatRequestDTO.getLocation())
                .changedBy(userDetail.getId())
                .changedByType(userDetail.getRole().name())
                .build();

        orderRepository.save(order);
        orderTrackingrepository.save(orderTracking);

        log.info(
                "Order status updated successfully. Order : {}, Previous : {}, Current : {}",
                order.getOrderNumber(),
                previousStatus,
                newStatus
        );
        if (newStatus == OrderStatus.DELIVERED) {

            eventPublisher.publishEvent(
                    new OrderDeliveredEvent(
                            order.getOrderNumber(),
                            userDetail.getUser(),
                            tenantId
                    )
            );
        }
        return "Order status updated successfully.";
    }

}
