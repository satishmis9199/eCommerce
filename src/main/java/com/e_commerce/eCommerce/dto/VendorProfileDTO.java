package com.e_commerce.eCommerce.dto;

import com.e_commerce.eCommerce.entity.SubscriptionPlan;
import com.e_commerce.eCommerce.entity.VendorStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorProfileDTO {

    private Long vendorId;

    private String tenantId;

    private String fullName;

    private String businessName;

    private String storeName;

    private String email;

    private String mobile;

    private String logo;

    private SubscriptionPlan subscriptionPlan;

    private VendorStatus status;

    private String role;

    private Boolean emailVerified;


    private LocalDateTime lastLogin;


}