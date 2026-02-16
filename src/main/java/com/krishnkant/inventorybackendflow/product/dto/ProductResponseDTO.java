package com.krishnkant.inventorybackendflow.product.dto;

public record ProductResponseDTO(
        Long id,
        String name,
        Double price,
        Integer stock,
        Boolean active
) {}
