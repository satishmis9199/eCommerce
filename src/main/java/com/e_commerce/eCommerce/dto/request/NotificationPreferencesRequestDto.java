package com.e_commerce.eCommerce.dto.request;

import lombok.Data;

@Data
public class NotificationPreferencesRequestDto {

    private boolean orderUpdatesEmail;
    private boolean promotionalOffers;
    private boolean smsNotifications;
}