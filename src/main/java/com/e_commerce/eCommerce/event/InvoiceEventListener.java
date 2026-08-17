package com.e_commerce.eCommerce.event;

import com.e_commerce.eCommerce.service.PdfInvoiceService;
import com.e_commerce.eCommerce.service.ProductSalesAsyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class InvoiceEventListener {

    private final PdfInvoiceService pdfInvoiceService;
    private final ProductSalesAsyncService productSalesAsyncService;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleOrderDelivered(
            OrderDeliveredEvent event) {

        log.error("Transaction committed.");

        pdfInvoiceService.generateInvoicePdfs(
                event.getOrderNumber(),
                event.getUser(),
                event.getTenantid()
        );
    }

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handlePlacingOrder(
            OrderTrackingEvent event) {
        productSalesAsyncService.createPlacedTracking(event.getOrderId(), event.getTenantId(), event.getVendorId());

        log.error("Transaction committed.");


    }

}