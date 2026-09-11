package com.e_commerce.eCommerce.scheduler;

import com.e_commerce.eCommerce.entity.EmailSubscriber;
import com.e_commerce.eCommerce.entity.Order;
import com.e_commerce.eCommerce.entity.Vendor;
import com.e_commerce.eCommerce.enums.ReminderType;
import com.e_commerce.eCommerce.repository.EmailSubscriberRepository;
import com.e_commerce.eCommerce.repository.OrderRepository;
import com.e_commerce.eCommerce.repository.VendorRepository;
import com.e_commerce.eCommerce.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsletterReminderScheduler {

    private final EmailSubscriberRepository newsletterSubscriberRepository;
    private final OrderRepository orderRepository;
    private final EmailService reminderEmailService;
    private final VendorRepository vendorRepository;

    private static final int MISS_YOU_THRESHOLD_DAYS = 30;


    private static final int SPECIAL_OFFER_THRESHOLD_DAYS = 60;


    private static final int MIN_GAP_BETWEEN_REMINDERS_DAYS = 15;


    @Scheduled(cron = "0 0 13 * * *")  // @Scheduled(fixedRate = 60000) // <- testing ke liye: har 1 minute me chalao
    public void runInactiveSubscriberReminders() {
        log.info("Newsletter reminder job started");

        List<String> tenantIds = newsletterSubscriberRepository.findAllTenantIdsWithActiveSubscribers();

        for (String tenantId : tenantIds) {
            processTenant(tenantId);
        }

        log.info("Newsletter reminder job finished");
    }

    private void processTenant(String tenantId) {
        List<EmailSubscriber> subscribers =
                newsletterSubscriberRepository.findByTenantIdAndSubscribedTrue(tenantId);
        Optional<Vendor> v2=vendorRepository.findByTenantId(tenantId);
        if(v2.isEmpty()){
            throw new RuntimeException("Vendor Not Found");
        }
        Vendor vendor=v2.get();
        String tenantName = vendor.getStoreName();

        for (EmailSubscriber subscriber : subscribers) {
            try {
                evaluateAndSend(tenantId, tenantName, subscriber);
            } catch (Exception e) {
                // Ek subscriber fail ho to poora batch mat roko
                log.error("Failed to process reminder for subscriber {} (tenant {}): {}",
                        subscriber.getEmail(), tenantId, e.getMessage());
            }
        }
    }

    private void evaluateAndSend(String tenantId, String tenantName, EmailSubscriber subscriber) {
        Optional<Vendor> v2=vendorRepository.findByTenantId(tenantId);
        if(v2.isEmpty()){
            throw new RuntimeException("Vendor Not Found");
        }
        Vendor vendor=v2.get();
        if (!canSendReminderNow(subscriber)) {
            return;
        }

//        Optional<Order> lastOrder = orderRepository
//                .findTopByTenantIdOrderByCreatedAtDesc(tenantId);
//        long daysSinceLastOrder = lastOrder
//                .map(order -> java.time.Duration.between(order.getCreatedAt(), LocalDateTime.now()).toDays())
//                .orElse(Long.MAX_VALUE);

        ReminderType typeToSend =ReminderType.MISS_YOU ;

//        if (daysSinceLastOrder >= SPECIAL_OFFER_THRESHOLD_DAYS) {
//            typeToSend = ReminderType.SPECIAL_OFFER;
//        } else if (daysSinceLastOrder >= MISS_YOU_THRESHOLD_DAYS) {
//            typeToSend = ReminderType.MISS_YOU;
//        }

        if (typeToSend == null) {
            return; // recently active hai, kuch mat bhejo
        }

        reminderEmailService.sendReminderEmail(subscriber.getEmail(), tenantName, typeToSend,vendor.getSubDomain());

        subscriber.setLastReminderSentAt(LocalDateTime.now());
        subscriber.setLastReminderType(typeToSend);
        newsletterSubscriberRepository.save(subscriber);

        log.info("Sent {} reminder to {} (tenant {})", typeToSend, subscriber.getEmail(), tenantId);
    }

    private boolean canSendReminderNow(EmailSubscriber subscriber) {
        if (subscriber.getLastReminderSentAt() == null) {
            return true;
        }
        long daysSinceLastReminder =
                java.time.Duration.between(subscriber.getLastReminderSentAt(), LocalDateTime.now()).toDays();
        return daysSinceLastReminder >= MIN_GAP_BETWEEN_REMINDERS_DAYS;
    }
}