package com.e_commerce.eCommerce.exception;

public class BrandDoesNotExist extends  RuntimeException{
    public BrandDoesNotExist(String message){
        super(message);
    }
}
