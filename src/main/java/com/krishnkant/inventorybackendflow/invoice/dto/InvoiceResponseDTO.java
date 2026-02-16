package com.krishnkant.inventorybackendflow.invoice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record InvoiceResponseDTO(

        String invoiceNumber,
        String orderReference,
        String transactionId,

        String customerName,
        String customerEmail,

        BigDecimal subtotal,
        BigDecimal discountAmount,
        BigDecimal finalAmount,

        String paymentStatus,
        String orderStatus,

        LocalDateTime issuedAt,

        List<InvoiceItemDTO> items
) {}
