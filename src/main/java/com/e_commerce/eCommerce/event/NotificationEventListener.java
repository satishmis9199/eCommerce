package com.e_commerce.eCommerce.event;

import com.e_commerce.eCommerce.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void createNotificationEvent(OrderCreatedEvent event) {

        log.info(
                "Order created event received | orderId={} | tenantId={} | vendorId={}",
                event.getOrderId(),
                event.getTenantId(),
                event.getVendorId()
        );

        notificationService.sendOrderNotification(event);
    }
}