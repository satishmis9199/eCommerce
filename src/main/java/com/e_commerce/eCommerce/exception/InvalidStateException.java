package com.e_commerce.eCommerce.exception;

public class InvalidStateException extends RuntimeException {
    public InvalidStateException(String message){
        super(message);
    }
}
