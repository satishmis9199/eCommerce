package com.e_commerce.eCommerce.exception;

/** Token hash not found, or malformed input — deliberately doesn't say which. */
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException() {
        super("Invalid or unrecognized token");
    }
}