package com.e_commerce.eCommerce.dto.request;

import com.e_commerce.eCommerce.enums.VendorRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class VendorRequestAdminDetailResponse {
    private Long id;
    private String requestCode;
    private String businessName;
    private String ownerName;
    private String email;
    private String phone;
    private String city;
    private String state;
    private String businessCategory;
    private String message;
    private VendorRequestStatus status;
    private String adminRemarks;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
    private String ipAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}