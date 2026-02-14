package com.krishnkant.inventorybackendflow.exception;

public class DuplicateOrderException extends RuntimeException {
    public DuplicateOrderException(String message) {
        super(message);
    }
}
