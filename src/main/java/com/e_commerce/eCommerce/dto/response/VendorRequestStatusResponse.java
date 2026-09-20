package com.e_commerce.eCommerce.dto.response;

import com.e_commerce.eCommerce.dto.request.VendorRequestTrackingResponse;
import com.e_commerce.eCommerce.enums.VendorRequestStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorRequestStatusResponse {

    private boolean success;

    private String requestCode;

    private VendorRequestStatus status;

    private String message;

    private List<VendorRequestTrackingResponse> tracking;
}