package com.e_commerce.eCommerce.exception;

import org.springframework.http.HttpStatus;

public class VendorRequestException extends RuntimeException {

    private final HttpStatus status;

    public VendorRequestException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}