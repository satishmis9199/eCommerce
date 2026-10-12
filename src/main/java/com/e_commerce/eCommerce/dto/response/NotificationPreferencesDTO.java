package com.e_commerce.eCommerce.dto.response;

import lombok.Data;

@Data
public class NotificationPreferencesDTO {

    private Preferences preferences;

    @Data
    public static class Preferences {

        private boolean orderUpdatesEmail;

        private boolean promotionalOffers;

        private boolean smsNotifications;
    }
}