package com.e_commerce.eCommerce.dto.request;

import com.e_commerce.eCommerce.enums.VendorRequestStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorRequestTrackingResponse {

    private VendorRequestStatus status;

    private String message;

    private LocalDateTime createdAt;
}