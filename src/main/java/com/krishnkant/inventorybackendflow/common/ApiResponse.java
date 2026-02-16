package com.krishnkant.inventorybackendflow.common;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import org.slf4j.MDC;

@Data
@Builder
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private int status;
    private LocalDateTime timestamp;
    private String requestId;

    public static <T> ApiResponse<T> success(T data, String message, int status) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .status(status)
                .timestamp(LocalDateTime.now())
                .requestId(MDC.get("requestId"))
                .build();
    }

    public static <T> ApiResponse<T> failure(String message, int status) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .status(status)
                .timestamp(LocalDateTime.now())
                .requestId(MDC.get("requestId"))
                .build();
    }

    public static <T> ApiResponse<T> failure(
            T data,
            String message,
            int status) {

        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(data)
                .status(status)
                .timestamp(LocalDateTime.now())
                .requestId(MDC.get("requestId"))
                .build();
    }
}
