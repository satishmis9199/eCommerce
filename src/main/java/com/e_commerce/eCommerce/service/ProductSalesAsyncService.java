package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.entity.Order;
import com.e_commerce.eCommerce.entity.OrderItem;
import com.e_commerce.eCommerce.entity.OrderStatus;
import com.e_commerce.eCommerce.entity.OrderTracking;
import com.e_commerce.eCommerce.repository.OrderRepository;
import com.e_commerce.eCommerce.repository.OrderTrackingrepository;
import com.e_commerce.eCommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductSalesAsyncService {
    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;
    private final OrderTrackingrepository orderTrackingrepository;

    private static final Logger logger =
            LoggerFactory.getLogger(ProductSalesAsyncService.class);

    @Async
    @Transactional
    public void updateSoldCount(List<OrderItem> items,String tenant) {
        try {
            if (items == null) {
                return;
            }
            if (items.isEmpty()) {
                return;
            }
            int count = 0;
            for (OrderItem item : items) {

                count++;
                if (item == null) {
                    continue;
                }
                Long productId = item.getProductId();
                if (productId == null) {
                    continue;
                }
                if (item.getQuantity() == null) {
                    continue;
                }

                Long quantity = Long.valueOf(item.getQuantity());
                try {
                    int updatedRows =
                            productRepository.incrementTotalSold(
                                    productId,
                                    quantity,tenant
                            );
                    if (updatedRows == 0) {

                        logger.error(
                                "WARNING :: No Product updated for productId = {}",
                                productId
                        );

                    } else {

                        logger.error(
                                "SUCCESS :: totalSold incremented by {} " +
                                        "for productId = {}",
                                quantity,
                                productId
                        );
                    }

                } catch (Exception dbException) {

                    logger.error(
                            "DB ERROR while updating productId = {}, quantity = {}",
                            productId,
                            quantity,
                            dbException
                    );

                    // Re-throw so transaction can rollback
                    throw dbException;
                }
            }


        } catch (Exception e) {

            logger.error(
                    "FAILED :: Exception inside updateSoldCount()",
                    e
            );
            throw e;

        } finally {
            logger.error("END :: ProductSalesAsyncService.updateSoldCount()");
        }
    }
    @Async
    @Transactional
    public void createPlacedTracking(Long orderId, String tenantId,Long vedorId) {
        try {

            if (orderId == null) {
                return;
            }

            if (tenantId == null || tenantId.isBlank()) {
                return;
            }

            Order order = orderRepository.findByidAndTenantId(orderId,tenantId);

            if (order == null) {
                return;
            }

            OrderTracking existing = orderTrackingrepository
                    .findByTenantIdAndOrderIdAndStatus(
                            tenantId,
                            orderId,
                            OrderStatus.PLACED
                    );

            if (existing != null) {
                return;
            }

            OrderTracking tracking = new OrderTracking();

            tracking.setTenantId(tenantId);
            tracking.setOrderId(orderId);
            tracking.setStatus(OrderStatus.PLACED);
            tracking.setRemarks("Order placed successfully");
            tracking.setCreatedAt(LocalDateTime.now());
            tracking.setVendorId(vedorId);
            tracking.setLocation("N/A");
            tracking.setChangedBy(1L);
            tracking.setChangedByType("System");


            orderTrackingrepository.save(tracking);
        } catch (Exception ex) {


            throw ex;

        } finally {

            log.info("END :: createPlacedTracking()");
            log.info("======================================================");

        }
    }
}
