package com.krishnkant.inventorybackendflow.exception;

public class StockNotAvailableException extends RuntimeException {
    public StockNotAvailableException(String message) {
        super(message);
    }
}
