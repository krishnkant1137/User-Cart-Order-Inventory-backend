package com.krishnkant.inventorybackendflow.common;

import lombok.Getter;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;
    private final int status;
    private final LocalDateTime timestamp;
    private final String requestId;

    private ApiResponse(
            boolean success,
            String message,
            T data,
            HttpStatus status) {

        this.success = success;
        this.message = message;
        this.data = data;
        this.status = status.value();
        this.timestamp = LocalDateTime.now();
        this.requestId = MDC.get("requestId");
    }

    public static <T> ApiResponse<T> success(
            T data,
            String message,
            HttpStatus status) {

        return new ApiResponse<>(true, message, data, status);
    }

    public static <T> ApiResponse<T> failure(
            String message,
            HttpStatus status) {

        return new ApiResponse<>(false, message, null, status);
    }

    public static <T> ApiResponse<T> failure(
            T data,
            String message,
            HttpStatus status) {

        return new ApiResponse<>(false, message, data, status);
    }
}
