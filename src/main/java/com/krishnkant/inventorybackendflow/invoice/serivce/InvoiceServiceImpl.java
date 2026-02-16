package com.krishnkant.inventorybackendflow.invoice.serivce;

import com.krishnkant.inventorybackendflow.exception.OrderNotFoundException;
import com.krishnkant.inventorybackendflow.invoice.dto.InvoiceItemDTO;
import com.krishnkant.inventorybackendflow.invoice.dto.InvoiceResponseDTO;
import com.krishnkant.inventorybackendflow.invoice.entity.Invoice;
import com.krishnkant.inventorybackendflow.invoice.repository.InvoiceRepository;
import com.krishnkant.inventorybackendflow.order.entity.Order;
import com.krishnkant.inventorybackendflow.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final OrderRepository orderRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository,
                              OrderRepository orderRepository) {
        this.invoiceRepository = invoiceRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public InvoiceResponseDTO getInvoice(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order not found"));

        Invoice invoice = invoiceRepository.findByOrder(order)
                .orElseThrow(() ->
                        new OrderNotFoundException("Invoice not generated yet"));

        return mapToResponse(invoice);
    }

    private InvoiceResponseDTO mapToResponse(Invoice invoice) {

        Order order = invoice.getOrder();

        List<InvoiceItemDTO> items =
                order.getItems().stream()
                        .map(item -> new InvoiceItemDTO(
                                item.getProductId(),
                                item.getProductName(),
                                item.getPrice(),
                                item.getQuantity(),
                                item.getTotalPrice()
                        ))
                        .toList();

        return new InvoiceResponseDTO(
                invoice.getInvoiceNumber(),
                order.getOrderReference(),
                order.getTransactionId(),
                order.getUser().getName(),
                order.getUser().getEmail(),
                order.getTotalAmount(),
                order.getDiscountAmount(),
                order.getFinalAmount(),
                order.getPaymentStatus().name(),
                order.getStatus().name(),
                invoice.getIssuedAt(),
                items
        );
    }
}
