package com.krishnkant.inventorybackendflow.product.dto;

import java.math.BigDecimal;

public record ProductResponseDTO(
        Long id,
        String name,
        BigDecimal price,
        Integer stock,
        Boolean active
) {}
