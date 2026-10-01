package com.e_commerce.eCommerce.exception;
public class CategoryNotFoundException extends RuntimeException {
    public   CategoryNotFoundException(String messge){
        super(messge);
    }
}
