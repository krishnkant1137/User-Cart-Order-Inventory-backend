package com.krishnkant.inventorybackendflow.common;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

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
                .requestId(UUID.randomUUID().toString())
                .build();
    }

    public static <T> ApiResponse<T> failure(String message, int status) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .status(status)
                .timestamp(LocalDateTime.now())
                .requestId(UUID.randomUUID().toString())
                .build();
    }
}
