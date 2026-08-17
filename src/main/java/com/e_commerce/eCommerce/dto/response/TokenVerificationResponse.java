package com.e_commerce.eCommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenVerificationResponse {

    public enum Status {VALID, EXPIRED, USED, INVALID}

    private final Status status;
    private final String emailMasked;

    public static TokenVerificationResponse of(Status status, String emailMasked) {
        return new TokenVerificationResponse(status, emailMasked);
    }
}