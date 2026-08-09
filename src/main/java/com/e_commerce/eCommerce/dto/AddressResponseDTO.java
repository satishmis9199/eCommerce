package com.e_commerce.eCommerce.dto;

import com.e_commerce.eCommerce.entity.AddressType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDTO {

    private Long id;

    private Long userId;

    private String tenantId;

    private String label;

    private String fullName;

    private String mobileNumber;

    private String alternateMobile;

    private String addressLine1;

    private String addressLine2;

    private String landmark;

    private String city;

    private String state;

    private String postalCode;

    private String country;

    private AddressType addressType;

    private Boolean isDefault;

    private Integer rowState;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}