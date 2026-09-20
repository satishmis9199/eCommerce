package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.dto.request.VendorRequestCreateRequest;
import com.e_commerce.eCommerce.dto.request.VendorRequestResponse;
import com.e_commerce.eCommerce.dto.response.VendorRequestStatusResponse;
import com.e_commerce.eCommerce.exception.VendorRequestException;
import com.e_commerce.eCommerce.service.VendorRequestService;
import jakarta.servlet.http.HttpServletRequest; // javax.servlet.http.HttpServletRequest on Spring Boot 2.x
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/public/vendor-requests")
@RequiredArgsConstructor
public class PublicVendorRequestController {

    private final VendorRequestService vendorRequestService;

    @PostMapping
    public ResponseEntity<VendorRequestResponse> submit(
            @RequestBody VendorRequestCreateRequest request,
            HttpServletRequest http) {
        try {
            String id = vendorRequestService.submit(request, clientIp(http));
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(VendorRequestResponse.ok("Vendor request submitted successfully", id));
        } catch (VendorRequestException e) {
            return ResponseEntity.status(e.getStatus()).body(VendorRequestResponse.error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(VendorRequestResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unable to submit vendor request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(VendorRequestResponse.error("Unable to submit vendor request"));
        }
    }

     private String clientIp(HttpServletRequest http) {
        String ip = http.getRemoteAddr();
        return ip != null && ip.length() > 45 ? ip.substring(0, 45) : ip;
    }

    @GetMapping("/{requestCode}/status")
    public ResponseEntity<VendorRequestStatusResponse> getRequestStatus(
            @PathVariable String requestCode
    ) {

        return ResponseEntity.ok(
                vendorRequestService.getStatus(requestCode)
        );
    }
}