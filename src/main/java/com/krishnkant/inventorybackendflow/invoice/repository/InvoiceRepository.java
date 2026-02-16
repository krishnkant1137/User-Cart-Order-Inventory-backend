package com.krishnkant.inventorybackendflow.invoice.repository;

import com.krishnkant.inventorybackendflow.invoice.entity.Invoice;
import com.krishnkant.inventorybackendflow.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByOrder(Order order);
}
