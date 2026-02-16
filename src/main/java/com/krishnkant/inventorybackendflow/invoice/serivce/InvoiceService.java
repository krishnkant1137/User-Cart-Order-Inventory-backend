package com.krishnkant.inventorybackendflow.invoice.serivce;

import com.krishnkant.inventorybackendflow.invoice.dto.InvoiceResponseDTO;

public interface InvoiceService {

    InvoiceResponseDTO getInvoice(Long orderId);
}