package com.e_commerce.eCommerce.dto;

import com.e_commerce.eCommerce.entity.OrderStatus;
import com.e_commerce.eCommerce.entity.PaymentMethod;
import com.e_commerce.eCommerce.entity.PaymentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class InvoiceGenrationDto {

    private String invoiceNumber;
    private LocalDateTime invoiceDate;
    private String orderNumber;
    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;

    private PaymentStatus paymentStatus;
    private OrderStatus orderStatus;
    private PaymentMethod paymentMethod;

    private String transactionId;
    private String referenceNumber;
    private String currencySymbol;

    private String thankYouMessage;
    private String termsAndConditions;
    private String returnPolicy;

    private LocalDateTime generatedAt;

    private CompanyDetailDTO company;
    private CustomerDetailDTo customer;
    private BillingAddressDto billingAddress;
    private VendorInvoiceDto vendor;
    private BillingAddressDto shippingAddress;

    private List<InvoiceItemsDTO> items;
    private InvoiceSummaryDTO summary;
}