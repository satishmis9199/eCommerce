package com.e_commerce.eCommerce.exception;

public class UnauthorizedException extends RuntimeException{
    public UnauthorizedException(String message){
        super((message));
    }
}
