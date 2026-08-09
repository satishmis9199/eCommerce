package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.dto.ApiResponse;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import com.e_commerce.eCommerce.service.PdfInvoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/u1/v1")
public class InvoiceController {

    private final PdfInvoiceService pdfInvoiceService;

    public InvoiceController(PdfInvoiceService pdfInvoiceService) {
        this.pdfInvoiceService = pdfInvoiceService;
    }


    @GetMapping(value = "/{orderId}/pdf", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<String>> downloadInvoice(@PathVariable String orderId, @AuthenticationPrincipal CustomUserDetail customUserDetail) {
       log.error("Inside Pdf Download");
        try{
            String pdfBytes = pdfInvoiceService.generateInvoicePdf(orderId, customUserDetail.getUser());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse<>(
                                    true,
                                    "Gemrated Pdf Url",
                                    pdfBytes

                            )
                    );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    e.getMessage(),
                                    ""
                            )
                    );
        }
    }
}
