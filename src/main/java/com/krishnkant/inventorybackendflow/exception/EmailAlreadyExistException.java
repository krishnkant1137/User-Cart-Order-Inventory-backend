package com.krishnkant.inventorybackendflow.exception;

public class EmailAlreadyExistException extends RuntimeException{
    public EmailAlreadyExistException(String message){
        super(message);
    }
}
