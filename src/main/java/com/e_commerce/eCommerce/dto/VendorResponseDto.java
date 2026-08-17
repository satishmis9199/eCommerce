package com.e_commerce.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorResponseDto {

    private Long vendorId;

    private String vendorName;

    private String vendorEmail;

    private String businessName;

    private String subscriptionPlan;

    private String status;

    private Long totalRevenue;

    private Integer totalOrders;
}