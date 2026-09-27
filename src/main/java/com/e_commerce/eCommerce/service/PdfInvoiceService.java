package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.config.R2Properties;
import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.*;
import com.e_commerce.eCommerce.entity.*;
import com.e_commerce.eCommerce.repository.*;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class PdfInvoiceService {

    private final TemplateEngine templateEngine;
    private final OrderRepository orderRepository;
    private final VendorRepository vendorRepository;
    private final InvoiceRepository invoiceRepository;
    private final OrderItemRepository orderItemRepository;
    private final VendorAddresss vendorAddresss;
    private final OrderAddressRepository orderAddressRepository;
    private final R2Properties r2Properties;
    private final FileStorageService fileStorageService;
    private final vendorBussinesss vendorBussinesssRepository;
    private final VendorBrandingRepository vendorBrandingRepository;
    private final UserRepos userRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    private String generateInvoicePassword(User user) {
        String firstName = (user.getFirstName() != null && !user.getFirstName().isBlank())
                ? user.getFirstName().trim().toUpperCase()
                : "USER";
        String namePart = firstName.length() >= 4 ? firstName.substring(0, 4) : firstName;

        String phoneDigits = (user.getPhone() != null)
                ? user.getPhone().replaceAll("\\D", "")
                : "0000";
        String phonePart = phoneDigits.length() >= 4
                ? phoneDigits.substring(phoneDigits.length() - 4)
                : String.format("%4s", phoneDigits).replace(' ', '0');

        return namePart + phonePart;
    }

    @Async
    @Transactional
    public void generateInvoicePdfs(String orderIds, User user, String tenantIds) {

        String tenantId = tenantIds;
        if (tenantId == null) {
            throw new RuntimeException("Invalid tenant");
        }
        if (user == null) {
            throw new RuntimeException("Please Login first...");
        }
        Vendor vendor = vendorRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new RuntimeException("No vendor found"));

        Order order = orderRepository.findByOrderNumberAndTenantIdAndVendorId(
                orderIds,
                tenantId,
                vendor.getId()
        );

        if (order == null) {
            throw new RuntimeException("Order does not exist");
        }
        Optional<User> user1 = userRepository.findByIdAndTenantId(order.getUserId(), tenantId);
        if (user1.isEmpty()) {
            throw new RuntimeException("User Does Not exist");
        }
        User user2 = user1.get();
        Long orderId = order.getId();
        if (order.getOrderStatus() != OrderStatus.DELIVERED) {
            throw new RuntimeException(" Invoice Can Be only Generated after a Order Delivery");
        }
        Optional<Invoice> existingInvoice = invoiceRepository.findByOrderId(orderId);
        VendorAddress vendorAddress = vendorAddresss.findByVendorId(vendor.getId());
        OrderAddress orderAddress = orderAddressRepository.findByOrderIdAndTenantId(order.getId(), tenantId);
        VendorBusiness vendorBusiness = vendorBussinesssRepository.findByVendorId(vendor.getId());
        VendorBranding vendorBranding = vendorBrandingRepository.findByVendorId(vendor.getId());

        if (orderAddress == null) {
            throw new RuntimeException("Order Address Not Found");
        }

        Invoice invoice;

        if (existingInvoice.isPresent() && existingInvoice.get().getStatus() == InvoiceStatus.GENERATED) {
            log.info("Invoice already generated for orderId={}", orderId);
            return;
        } else if (existingInvoice.isPresent() && existingInvoice.get().getStatus() != InvoiceStatus.GENERATED) {
            invoice = existingInvoice.get();
            invoice.setStatus(InvoiceStatus.GENERATING);
            invoice.setGeneratedAt(LocalDateTime.now());
        } else {
            invoice = new Invoice();
            invoice.setInvoiceNumber("INV-" + System.currentTimeMillis());
            invoice.setOrderId(orderId);
            invoice.setTenantId(tenantId);
            invoice.setVendorId(vendor.getId());
            invoice.setStatus(InvoiceStatus.GENERATING);
            invoice.setGeneratedAt(LocalDateTime.now());
        }

        invoice = invoiceRepository.save(invoice);
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderIdAndTenantId(orderId, tenantId);

        List<Map<String, Object>> items = new ArrayList<>();

        BigDecimal totalMrp = BigDecimal.ZERO;
        BigDecimal sellingTotal = BigDecimal.ZERO;
        List<InvoiceItemsDTO> invoiceItemsDTOS = new ArrayList<>();
        InvoiceSummaryDTO invoiceSummaryDTO = new InvoiceSummaryDTO();
        for (OrderItem orderItem : orderItems) {

            long quantity = orderItem.getQuantity();

            BigDecimal sellingAmount = orderItem.getSellingPrice().multiply(BigDecimal.valueOf(quantity));
            BigDecimal mrpAmount = orderItem.getMrp().multiply(BigDecimal.valueOf(quantity));

            InvoiceItemsDTO invoiceItemsDTO = new InvoiceItemsDTO();
            invoiceItemsDTO.setProductName(orderItem.getProductName());
            invoiceItemsDTO.setDescription(orderItem.getDescription());
            invoiceItemsDTO.setImageUrl(r2Properties.getPublicUrl() + "/" + orderItem.getImageUrl());
            invoiceItemsDTO.setHsnCode(orderItem.getHsnCode());
            invoiceItemsDTO.setQuantity(String.valueOf(orderItem.getQuantity()));
            invoiceItemsDTO.setUnit("");
            invoiceItemsDTO.setMrp(String.valueOf(orderItem.getMrp()));
            invoiceItemsDTO.setDiscountAmount(String.valueOf(orderItem.getMrp().subtract(orderItem.getSellingPrice())));
            invoiceItemsDTO.setSellingPrice(String.valueOf(orderItem.getSellingPrice()));
            invoiceItemsDTO.setTaxAmount("0");
            invoiceItemsDTO.setTotalAmount(String.valueOf(orderItem.getLineTotal()));
            invoiceItemsDTOS.add(invoiceItemsDTO);

            sellingTotal = sellingTotal.add(sellingAmount);
            totalMrp = totalMrp.add(mrpAmount);
        }

        BigDecimal discount = totalMrp.subtract(sellingTotal);
        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            discount = BigDecimal.ZERO;
        }

        BigDecimal cgst = BigDecimal.ZERO;
        BigDecimal sgst = BigDecimal.ZERO;
        BigDecimal igst = BigDecimal.ZERO;
        BigDecimal shippingFee = BigDecimal.ZERO;
        BigDecimal grandTotal = sellingTotal.add(cgst).add(sgst).add(igst).add(shippingFee);

        PaymentStatus paymentStatus = order.getPaymentStatus();
        if (paymentStatus == PaymentStatus.PAID) {
            invoiceSummaryDTO.setAmountPaid(grandTotal);
            invoiceSummaryDTO.setAmountDue(BigDecimal.ZERO);
        } else {
            invoiceSummaryDTO.setAmountPaid(BigDecimal.ZERO);
            invoiceSummaryDTO.setAmountDue(grandTotal);
        }

        invoiceSummaryDTO.setTotalDiscount(discount);
        invoiceSummaryDTO.setSubtotal(totalMrp);
        invoiceSummaryDTO.setShippingCharge(shippingFee);
        invoiceSummaryDTO.setPlatformCharge(BigDecimal.ZERO);
        invoiceSummaryDTO.setGrandTotal(grandTotal);
        invoiceSummaryDTO.setTotalTax(BigDecimal.ZERO);

        InvoiceGenrationDto invoiceData = new InvoiceGenrationDto();
        CompanyDetailDTO companyDetailDTO = new CompanyDetailDTO();
        CustomerDetailDTo customerDetailDTo = new CustomerDetailDTo();
        BillingAddressDto billingAddressDto = new BillingAddressDto();
        BillingAddressDto shippingAddress = new BillingAddressDto();
        VendorInvoiceDto vendorInvoiceDto = new VendorInvoiceDto();

        invoiceData.setInvoiceNumber(invoice.getInvoiceNumber());
        invoiceData.setInvoiceDate(LocalDateTime.now());
        invoiceData.setOrderNumber(order.getOrderNumber());
        invoiceData.setOrderDate(order.getCreatedAt());
        invoiceData.setDeliveryDate(order.getCreatedAt().plusDays(1));
        invoiceData.setPaymentMethod(order.getPaymentMethod());
        invoiceData.setPaymentStatus(order.getPaymentStatus());
        invoiceData.setOrderStatus(order.getOrderStatus());
        invoiceData.setTransactionId(order.getPaymentReferenceId());
        invoiceData.setReferenceNumber(order.getPaymentReferenceId());
        invoiceData.setCurrencySymbol("Rs.");
        invoiceData.setThankYouMessage("Thank you for choosing " + vendor.getBussinessName() + " Supplies");
        invoiceData.setTermsAndConditions("Payment is due within 15 days from the invoice date. Goods once dispatched cannot be exchanged unless damaged in transit. All disputes are subject to Lucknow jurisdiction only");
        invoiceData.setReturnPolicy("Returns accepted within 7 days of delivery for unopened cement bags, unused steel, and undamaged fittings. Custom-cut or made-to-order materials are non-returnable");
        invoiceData.setGeneratedAt(invoice.getGeneratedAt());

        companyDetailDTO.setCompanyName(vendor.getBussinessName());
        companyDetailDTO.setLogoUrl(vendor.getLogo());
        companyDetailDTO.setAddressLine1(vendorAddress.getAddressLine1());
        companyDetailDTO.setAddressLine2(vendorAddress.getAddressLine2());
        companyDetailDTO.setCity(vendorAddress.getCity());
        companyDetailDTO.setState(vendorAddress.getCity());
        companyDetailDTO.setPincode(vendorAddress.getPostalCode());

        if (vendorBranding != null) {
            companyDetailDTO.setPhone(vendorBranding.getSupportPhone());
            companyDetailDTO.setEmail(vendorBranding.getSupportEmail());
        } else {
            companyDetailDTO.setPhone("N/A");
            companyDetailDTO.setEmail(vendor.getEmail());
        }

        if (vendorBusiness == null) {
            companyDetailDTO.setWebsite("N/A");
            companyDetailDTO.setGstin("N/A");
        } else {
            companyDetailDTO.setWebsite(vendorBusiness.getWebsite());
            companyDetailDTO.setGstin(vendorBusiness.getGstNumber());
        }

        customerDetailDTo.setName(user2.getFirstName() + " " + user.getLastName());
        customerDetailDTo.setPhone(user2.getPhone());
        customerDetailDTo.setEmail(user2.getEmail());

        billingAddressDto.setLine1(orderAddress.getAddressLine1());
        billingAddressDto.setLine2(orderAddress.getAddressLine2());
        billingAddressDto.setState(orderAddress.getState());
        billingAddressDto.setCity(orderAddress.getCity());
        billingAddressDto.setPincode(orderAddress.getPostalCode());
        billingAddressDto.setLandmark(orderAddress.getLandmark() != null ? orderAddress.getLandmark() : "N/A");

        shippingAddress.setLine1(orderAddress.getAddressLine1());
        shippingAddress.setLine2(orderAddress.getAddressLine2());
        shippingAddress.setState(orderAddress.getState());
        shippingAddress.setCity(orderAddress.getCity());
        shippingAddress.setPincode(orderAddress.getPostalCode());
        shippingAddress.setLandmark(orderAddress.getLandmark() != null ? orderAddress.getLandmark() : "N/A");

        vendorInvoiceDto.setName(vendor.getFirstName());

        invoiceData.setCustomer(customerDetailDTo);
        invoiceData.setCompany(companyDetailDTO);
        invoiceData.setShippingAddress(billingAddressDto);
        invoiceData.setBillingAddress(billingAddressDto);
        invoiceData.setItems(invoiceItemsDTOS);
        invoiceData.setSummary(invoiceSummaryDTO);
        invoiceData.setVendor(vendorInvoiceDto);

        Context context = new Context();
        context.setVariable("invoice", invoiceData);
        context.setVariable("shipping", null);
        context.setVariable("bank", null);

        String renderedHtml;
        try {
            renderedHtml = templateEngine.process("invoice", context);
        } catch (Exception e) {
            log.error("Failed to render invoice HTML for orderId={}", orderId, e);
            invoice.setStatus(InvoiceStatus.FAILED);
            invoiceRepository.save(invoice);
            throw new RuntimeException("Failed to render invoice HTML", e);
        }

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(renderedHtml, null);
            builder.toStream(os);
            builder.run();

            byte[] pdfBytes = os.toByteArray();

            String password = generateInvoicePassword(user2);
            log.info("Invoice password generated for orderId={}", orderId);

            byte[] encryptedPdfBytes;
            try (PDDocument document = PDDocument.load(pdfBytes)) {
                AccessPermission accessPermission = new AccessPermission();
                // owner password = user password => opening the PDF itself will ask for this password
                StandardProtectionPolicy protectionPolicy =
                        new StandardProtectionPolicy(password, password, accessPermission);
                protectionPolicy.setEncryptionKeyLength(128);
                document.protect(protectionPolicy);

                try (ByteArrayOutputStream encryptedOut = new ByteArrayOutputStream()) {
                    document.save(encryptedOut);
                    encryptedPdfBytes = encryptedOut.toByteArray();
                }
            }

            String objectKey = fileStorageService.upload(
                    encryptedPdfBytes,
                    invoice.getInvoiceNumber() + ".pdf",
                    "application/pdf",
                    "invoices/" + tenantId
            );

            invoice.setStatus(InvoiceStatus.GENERATED);
            invoice.setGeneratedAt(LocalDateTime.now());
            invoice.setPdfUrl(r2Properties.getPublicUrl() + "/" + objectKey);
            invoice.setPdfKey(objectKey);
            invoiceRepository.save(invoice);
        } catch (Exception e) {
            log.error("Failed to generate invoice PDF for orderId={}", orderId, e);
            invoice.setStatus(InvoiceStatus.FAILED);
            invoiceRepository.save(invoice);
            throw new RuntimeException("Failed to generate invoice PDF", e);
        }
    }

    public String generateInvoicePdf(String orderIds, User user) {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Invalid tenant");
        }
        if (user == null) {
            throw new RuntimeException("Please Login first...");
        }
        Vendor vendor = vendorRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new RuntimeException("No vendor found"));

        Order order = orderRepository.findByOrderNumberAndTenantIdAndVendorIdAndUserId(
                orderIds,
                tenantId,
                vendor.getId(),
                user.getId()
        );

        if (order == null) {
            throw new RuntimeException("Order does not exist");
        }
        Long orderId = order.getId();
        Optional<Invoice> existingInvoice = invoiceRepository.findByOrderId(orderId);

        if (order.getOrderStatus() != OrderStatus.DELIVERED) {
            throw new RuntimeException("Invoice can be downloaded only after the order is delivered.");
        }

        if (existingInvoice.isEmpty()) {
            throw new RuntimeException("Invoice is not generated yet. Please try again after some time.");
        }

        Invoice invoice = existingInvoice.get();
        if (invoice.getStatus() == InvoiceStatus.GENERATING
                && invoice.getGeneratedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {

            log.warn("Invoice generation stuck for orderId={}, retrying", orderId);
            invoice.setStatus(InvoiceStatus.FAILED);
            invoiceRepository.save(invoice);

            generateInvoicePdfs(orderIds, user, tenantId);

            throw new RuntimeException("Please try in 2 minutes");
        }

        return existingInvoice.get().getPdfUrl();
    }
}