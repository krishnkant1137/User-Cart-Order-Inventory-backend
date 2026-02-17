package com.krishnkant.inventorybackendflow.invoice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record InvoiceResponseDTO(

        String invoiceNumber,
        String orderReference,
        String transactionId,
        LocalDateTime orderPlacedAt,
        String paymentMethod,
        String deliveryAddress,

        String customerName,
        String customerEmail,

        BigDecimal subtotal,
        BigDecimal discountAmount,
        BigDecimal finalAmount,
        BigDecimal taxAmount,
        BigDecimal shippingAmount,

        String paymentStatus,
        String orderStatus,
        LocalDateTime estimatedDeliveryDate,

        LocalDateTime issuedAt,

        List<InvoiceItemDTO> items
) {}
