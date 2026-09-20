package com.e_commerce.eCommerce.dto.request;

import com.e_commerce.eCommerce.enums.VendorRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class VendorRequestAdminListResponse {
    private Long id;
    private String requestCode;
    private String businessName;
    private String ownerName;
    private String email;
    private String phone;
    private String city;
    private String state;
    private String businessCategory;
    private VendorRequestStatus status;
    private LocalDateTime createdAt;
}