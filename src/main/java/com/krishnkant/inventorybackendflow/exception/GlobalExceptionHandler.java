package com.krishnkant.inventorybackendflow.exception;

import com.krishnkant.inventorybackendflow.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ApiResponse<?>> buildErrorResponse(
            String message,
            HttpStatus status) {

        log.warn("Business Exception: {} - Status: {}", message, status);

        return ResponseEntity.status(status)
                .body(ApiResponse.failure(message, status));
    }

    @ExceptionHandler(StockNotAvailableException.class)
    public ResponseEntity<ApiResponse<?>> handleStock(StockNotAvailableException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleCartNotFound(CartNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleProductNotFound(ProductNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateOrderException.class)
    public ResponseEntity<ApiResponse<?>> handleDuplicateOrder(DuplicateOrderException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmptyCartException.class)
    public ResponseEntity<ApiResponse<?>> handleEmptyCart(EmptyCartException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleUserNotFound(UserNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<ApiResponse<?>> handleEmailAlreadyExist(EmailAlreadyExistException ex) {
        return buildErrorResponse("Email already exists", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleOrderNotFound(OrderNotFoundException ex) {
        return buildErrorResponse("Email already exists", HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse<?>> handleOptimisticLock(
            ObjectOptimisticLockingFailureException ex) {

        return buildErrorResponse(
                "Record was modified by another transaction. Please retry.",
                HttpStatus.CONFLICT
        );
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );

        log.warn("Validation failed: {}", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure(
                        errors,
                        "Validation failed",
                        HttpStatus.BAD_REQUEST
                ));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<?>> handleMissingParam(
            MissingServletRequestParameterException ex) {

        return buildErrorResponse(
                ex.getParameterName() + " parameter is required",
                HttpStatus.BAD_REQUEST
        );
    }




    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception ex) {

        log.error("Unhandled exception occurred", ex);

        return buildErrorResponse(
                "Something went wrong. Please try again later.",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
