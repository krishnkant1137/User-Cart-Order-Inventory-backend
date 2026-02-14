package com.krishnkant.inventorybackendflow.exception;

import com.krishnkant.inventorybackendflow.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StockNotAvailableException.class)
    public ResponseEntity<ApiResponse<?>> handleStock(
            StockNotAvailableException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure(
                        ex.getMessage(),
                        HttpStatus.BAD_REQUEST.value()
                ));
    }


    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleCartNotFound(
            CartNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.failure(
                        ex.getMessage(),
                        HttpStatus.NOT_FOUND.value()
                ));
    }


    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleProductNotFound(
            ProductNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.failure(
                        ex.getMessage(),
                        HttpStatus.NOT_FOUND.value()
                ));
    }


    @ExceptionHandler(DuplicateOrderException.class)
    public ResponseEntity<ApiResponse<?>> handleDuplicateOrder(
            DuplicateOrderException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.failure(
                        ex.getMessage(),
                        HttpStatus.CONFLICT.value()
                ));
    }


    @ExceptionHandler(EmptyCartException.class)
    public ResponseEntity<ApiResponse<?>> handleEmptyCart(
            EmptyCartException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure(
                        ex.getMessage(),
                        HttpStatus.BAD_REQUEST  .value()
                ));
    }


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleUserNotFound(
            UserNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.failure(
                        ex.getMessage(),
                        HttpStatus.NOT_FOUND.value()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception ex) {

        log.error("Unhandled exception occurred", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure(
                        "Something went wrong. Please try again later.",
                        HttpStatus.INTERNAL_SERVER_ERROR.value()
                ));
    }


}
