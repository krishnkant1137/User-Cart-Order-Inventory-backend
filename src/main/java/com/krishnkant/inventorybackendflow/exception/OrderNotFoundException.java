package com.krishnkant.inventorybackendflow.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message){
        super(message);
    }
}
