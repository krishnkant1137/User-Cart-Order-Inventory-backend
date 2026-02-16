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

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse<?>> handleOptimisticLock(
            ObjectOptimisticLockingFailureException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.failure(
                        "Record was modified by another transaction. Please retry.",
                        HttpStatus.CONFLICT.value()
                ));
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<ApiResponse<?>> handleEmailAlreadyExist(
            EmailAlreadyExistException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.failure(
                        "Email already exists",
                        HttpStatus.CONFLICT.value()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(),
                                error.getDefaultMessage())
                );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure(
                        errors,
                        "Validation failed",
                        HttpStatus.BAD_REQUEST.value()
                ));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<?>> handleMissingParam(
            MissingServletRequestParameterException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure(
                        ex.getParameterName() + " parameter is required",
                        HttpStatus.BAD_REQUEST.value()
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
