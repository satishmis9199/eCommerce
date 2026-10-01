package com.e_commerce.eCommerce.exception;

public class TenantNoFoundException extends RuntimeException{
    public TenantNoFoundException(String message){
        super(message);
    }
}
