package com.krishnkant.inventorybackendflow.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequestDTO(

        @NotBlank(message = "Name is required")
        String name,

        @Email(message = "Invalid email")
        @NotBlank(message = "Email is required")
        String email
) {}
