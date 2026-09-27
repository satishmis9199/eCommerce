package com.e_commerce.eCommerce.exception;

public class vendorNotFoundException extends RuntimeException {
    public vendorNotFoundException(String message){
        super(message);
    }
}
