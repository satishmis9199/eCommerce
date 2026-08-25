package com.e_commerce.eCommerce.exception;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException() {
        super("This link has expired ..Please Reset again" );
    }
}