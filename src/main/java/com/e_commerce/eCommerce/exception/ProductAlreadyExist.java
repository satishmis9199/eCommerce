package com.e_commerce.eCommerce.exception;

public class ProductAlreadyExist extends RuntimeException{
    public ProductAlreadyExist(String message){
        super(message);
    }
}
