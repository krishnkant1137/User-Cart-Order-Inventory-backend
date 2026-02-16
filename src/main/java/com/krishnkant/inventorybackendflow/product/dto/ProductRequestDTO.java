package com.krishnkant.inventorybackendflow.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductRequestDTO(

        @NotBlank(message = "Product name is required")
        String name,

        @NotNull(message = "Price is required")
        @Min(value = 1, message = "Price must be greater than 0")
        Double price,

        @NotNull(message = "Stock is required")
        @Min(value = 0, message = "Stock cannot be negative")
        Integer stock
) {}
