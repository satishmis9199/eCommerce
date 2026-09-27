package com.e_commerce.eCommerce.exception;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException() {
        super("Invalid or unrecognized token");
    }
}