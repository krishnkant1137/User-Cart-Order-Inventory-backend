package com.krishnkant.inventorybackendflow.exception;

public class EmptyCartException extends RuntimeException {
    public EmptyCartException(String message) {
        super(message);
    }
}
