package com.ecommerce.invoice.service.impl;

import com.ecommerce.invoice.entity.Invoice;
import com.ecommerce.invoice.repository.InvoiceRepository;
import com.ecommerce.invoice.service.InvoiceService;
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
        Optional<Invoice> optionalInvoice = invoiceRepository.findById(invoiceId);
        return optionalInvoice.orElse(null);
    }

    @Override
    public Invoice createInvoice(Invoice invoice) {
        if (invoice.getInvoiceId() == null)
            invoice.setInvoiceId(UUID.randomUUID().toString());
        else {
            Optional<Invoice> existingInvoice = invoiceRepository.findById(invoice.getInvoiceId());
            if (existingInvoice.isPresent()) {
                throw new RuntimeException("Invoice with ID " + invoice.getInvoiceId() + " already exists.");
            }
        }
        return invoiceRepository.saveAndFlush(invoice);
    }

    @Override
    public Invoice updateInvoice(Invoice updatedInvoice) {
        return invoiceRepository.saveAndFlush(updatedInvoice);
    }

    @Override
    public void patchInvoice(String invoiceId, Map<String, Object> updates) {
        Invoice existingInvoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        updates.forEach((key, value) -> {
            try {
                Field field = Invoice.class.getDeclaredField(key);
                field.setAccessible(true);
                if (field.getType() == Timestamp.class) {
                    try {
                        OffsetDateTime odt = OffsetDateTime.parse((String) value);
                        Timestamp timestampValue = Timestamp.from(odt.toInstant());
                        field.set(existingInvoice, timestampValue);
                    } catch (DateTimeParseException e) {
                        throw new IllegalArgumentException("Invalid format for " + key + " TimeStamp field");
                    }
                }
                else if (field.getType().equals(BigDecimal.class)) {
                    value = new BigDecimal((Integer) value);
                    field.set(existingInvoice, value);
                }
                else {
                    field.set(existingInvoice, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalArgumentException("Invalid update field: " + key);
            }
        });

        invoiceRepository.save(existingInvoice);
    }

    @Override
    public void deleteInvoice(String invoiceId) {
        invoiceRepository.deleteById(invoiceId);
    }
}
