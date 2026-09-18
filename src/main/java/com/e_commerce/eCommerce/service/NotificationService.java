package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.dto.request.OrderNotificationDTO;
import com.e_commerce.eCommerce.dto.response.NotificationDto;
import com.e_commerce.eCommerce.dto.response.NotificationListResponse;
import com.e_commerce.eCommerce.entity.Notification;
import com.e_commerce.eCommerce.enums.NotificationType;
import com.e_commerce.eCommerce.event.OrderCreatedEvent;
import com.e_commerce.eCommerce.event.VendorNotificationEvent;
import com.e_commerce.eCommerce.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;

    @Async
    public void saveNotification(
            String tenantId,
            Long vendorId,
            Long orderId,
            NotificationType type,
            String title,
            String message) {

        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("Tenant ID is required");
        }

        Notification notification =
                Notification.builder()
                        .tenantId(tenantId)
                        .vendorId(vendorId)
                        .orderId(orderId)
                        .type(type)
                        .title(title)
                        .message(message)
                        .isRead(false)
                        .build();

        notificationRepository.save(notification);
    }

    public void sendOrderNotification(OrderCreatedEvent event) {

        try {
            OrderNotificationDTO notification =
                    OrderNotificationDTO.builder()
                            .orderId(event.getOrderId())
                            .tenantId(event.getTenantId())
                            .vendorId(event.getVendorId())
                            .totalAmount(event.getTotalAmount())
                            .type(String.valueOf(NotificationType.NEW_ORDER))
                            .title("New Order Received")
                            .message(
                                    "Order #" + event.getOrderId()
                                            + " has been received."
                            )
                            .build();

            String destination =
                    "/topic/vendor/"
                            + event.getTenantId()
                            + "/notifications";

            messagingTemplate.convertAndSend(
                    destination,
                    notification
            );

        } catch (Exception e) {
            log.error(
                    "Failed to send order notification for orderId={}",
                    event.getOrderId(),
                    e
            );
        }
    }

    public void sendVendorNotification(
            VendorNotificationEvent vendorNotificationEvent
    ) {

        try {
            log.error("Generic notification event Started");

            OrderNotificationDTO notification =
                    OrderNotificationDTO.builder()
                            .orderId(null)
                            .tenantId(vendorNotificationEvent.getTenantId())
                            .vendorId(vendorNotificationEvent.getVendorId())
                            .totalAmount(null)
                            .type(String.valueOf(vendorNotificationEvent.getNotificationType()))
                            .title(vendorNotificationEvent.getTitle())
                            .message(vendorNotificationEvent.getMessage())
                            .build();

            String destination =
                    "/topic/vendor/"
                            + vendorNotificationEvent.getTenantId()
                            + "/notifications";

            messagingTemplate.convertAndSend(
                    destination,
                    notification
            );
            log.error("Generic notification event send");

        } catch (Exception e) {

            log.error(
                    "Failed to send vendor notification",
                    e
            );
        }
    }

    public NotificationListResponse getNotifications(
            String tenantId,
            int page,
            int size) {

        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException(
                    "Tenant ID cannot be null or blank"
            );
        }

        if (page < 0) {
            page = 0;
        }

        if (size <= 0) {
            size = 20;
        }

        if (size > 100) {
            size = 100;
        }

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Sort.Direction.DESC,
                                "createdAt"
                        )
                );

        Page<Notification> notificationPage =
                notificationRepository
                        .findByTenantIdOrderByCreatedAtDesc(
                                tenantId,
                                pageable
                        );

        List<NotificationDto> data =
                notificationPage
                        .getContent()
                        .stream()
                        .map(NotificationDto::fromEntity)
                        .collect(Collectors.toList());

        long unreadCount =
                notificationRepository
                        .countByTenantIdAndIsReadFalse(tenantId);

        return NotificationListResponse
                .builder()
                .unreadCount(unreadCount)
                .data(data)
                .build();
    }

    public boolean markAsRead(
            Long id,
            String tenantId) {

        if (id == null || tenantId == null || tenantId.isBlank()) {
            return false;
        }

        if (!notificationRepository
                .existsByIdAndTenantId(id, tenantId)) {
            return false;
        }

        notificationRepository
                .findById(id)
                .ifPresent(notification -> {
                    notification.setRead(true);
                    notificationRepository.save(notification);
                });

        return true;
    }
    @Transactional
    public int markAllAsRead(String tenantId) {

        if (tenantId == null || tenantId.isBlank()) {
            return 0;
        }

        return notificationRepository
                .markAllAsReadForTenant(tenantId);
    }

    public boolean delete(
            Long id,
            String tenantId) {

        if (id == null || tenantId == null || tenantId.isBlank()) {
            return false;
        }

        if (!notificationRepository
                .existsByIdAndTenantId(id, tenantId)) {
            return false;
        }

        notificationRepository.deleteById(id);

        return true;
    }
}
