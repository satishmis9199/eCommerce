package com.e_commerce.eCommerce.exception;

public class TokenAlreadyUsedException extends RuntimeException {
    public TokenAlreadyUsedException() {
        super("This link has already been used");
    }
}