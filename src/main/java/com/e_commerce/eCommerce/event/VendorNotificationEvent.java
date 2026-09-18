package com.e_commerce.eCommerce.event;

import com.e_commerce.eCommerce.enums.NotificationType;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class VendorNotificationEvent {

    private String tenantId;
    private Long vendorId;
    private NotificationType notificationType;
    private String title;
    private String message;
}