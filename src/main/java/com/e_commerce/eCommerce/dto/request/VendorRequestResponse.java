package com.e_commerce.eCommerce.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VendorRequestResponse {

    private boolean success;
    private String message;
    private String requestCode;

    public static VendorRequestResponse ok(String message, String requestId) {
        return new VendorRequestResponse(true, message, requestId);
    }

    public static VendorRequestResponse error(String message) {
        return new VendorRequestResponse(false, message, null);
    }
}