package com.krishnkant.inventorybackendflow.user.dto;

public record UserResponseDTO(
        Long id,
        String name,
        String email,
        Boolean active
) {}
