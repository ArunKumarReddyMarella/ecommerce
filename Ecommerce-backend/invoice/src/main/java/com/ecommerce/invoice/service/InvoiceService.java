package com.ecommerce.invoice.service;

import com.ecommerce.invoice.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.Optional;

public interface InvoiceService {
    Page<Invoice> getInvoices(Pageable pageable);
    Invoice getInvoiceById(String invoiceId);
    Invoice createInvoice(Invoice invoice);
    Invoice updateInvoice(Invoice invoice);
    Invoice patchInvoice(String invoiceId, Map<String, Object> updates);
    void deleteInvoice(String invoiceId);

    Invoice getInvoiceByTransactionId(String transactionId);
}
