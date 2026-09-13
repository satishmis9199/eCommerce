package com.e_commerce.eCommerce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreSettingsRequestDto {

    private String storeStatus;

    private String closeReason;

    private boolean maintenanceMode;

    private String currency;

    private String timezone;

    private String language;
}