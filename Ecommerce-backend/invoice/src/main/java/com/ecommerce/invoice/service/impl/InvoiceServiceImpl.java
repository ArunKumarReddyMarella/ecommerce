package com.ecommerce.invoice.service.impl;

import com.ecommerce.invoice.entity.Invoice;
import com.ecommerce.invoice.exception.InvoiceAlreadyExistsException;
import com.ecommerce.invoice.exception.InvoiceNotFoundException;
import com.ecommerce.invoice.repository.InvoiceRepository;
import com.ecommerce.invoice.service.InvoiceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Override
    public Page<Invoice> getInvoices(Pageable pageable) {
        return invoiceRepository.findAll(pageable);
    }

    @Override
    public Invoice getInvoiceById(String invoiceId) {
        Invoice optionalInvoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with ID: " + invoiceId));
        return optionalInvoice;
    }

    @Override
    public Invoice createInvoice(Invoice invoice) {
        if (invoice.getInvoiceId() == null)
            invoice.setInvoiceId(UUID.randomUUID().toString());
        else {
            Optional<Invoice> existingInvoice = invoiceRepository.findById(invoice.getInvoiceId());
            if (existingInvoice.isPresent()) {
                throw new InvoiceAlreadyExistsException("Invoice with ID " + invoice.getInvoiceId() + " already exists.");
            }
        }
        return invoiceRepository.saveAndFlush(invoice);
    }

    @Override
    public Invoice updateInvoice(Invoice updatedInvoice) {
        if(!invoiceRepository.existsById(updatedInvoice.getInvoiceId())) {
            throw new InvoiceNotFoundException("Invoice not found with ID: " + updatedInvoice.getInvoiceId());
        }
        return invoiceRepository.saveAndFlush(updatedInvoice);
    }

    @Override
    public void patchInvoice(String invoiceId, Map<String, Object> updates) {
        Invoice existingInvoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with ID: " + invoiceId));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // for Timestamp support

        try {
            // assuming updates is a Map<String, Object>
            mapper.updateValue(existingInvoice, updates);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid update field: " + updates);
        }
        invoiceRepository.save(existingInvoice);
    }

    @Override
    public void deleteInvoice(String invoiceId) {
        if(!invoiceRepository.existsById(invoiceId)) {
            throw new InvoiceNotFoundException("Invoice not found with ID: " + invoiceId);
        }
            invoiceRepository.deleteById(invoiceId);
    }
}
