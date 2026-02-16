package com.krishnkant.inventorybackendflow.invoice.controller;

import com.krishnkant.inventorybackendflow.common.ApiResponse;
import com.krishnkant.inventorybackendflow.invoice.dto.InvoiceResponseDTO;
import com.krishnkant.inventorybackendflow.invoice.serivce.InvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<InvoiceResponseDTO>> getInvoice(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        invoiceService.getInvoice(orderId),
                        "Invoice fetched successfully",
                        HttpStatus.OK
                )
        );
    }
}
