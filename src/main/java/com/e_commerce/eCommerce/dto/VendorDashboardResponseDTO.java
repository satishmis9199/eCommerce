package com.e_commerce.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorDashboardResponseDTO {

    private boolean success;

    private String message;

    private VendorProfileDTO vendorProfile;

}