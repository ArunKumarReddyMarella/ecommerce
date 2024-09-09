package com.ecommerce.invoice.controller;

import com.ecommerce.invoice.entity.Invoice;
import com.ecommerce.invoice.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public ResponseEntity<Page<Invoice>> getInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "paymentDate");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Invoice> invoices = invoiceService.getInvoices(pageable);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable String invoiceId) {
        Invoice invoice = invoiceService.getInvoiceById(invoiceId);
        return ResponseEntity.ok(invoice);
    }

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@RequestBody @Valid Invoice invoice) {
        Invoice createdInvoice = invoiceService.createInvoice(invoice);
        return ResponseEntity.ok(createdInvoice);
    }

    @PutMapping("/{invoiceId}")
    public ResponseEntity<Invoice> updateInvoice(@PathVariable String invoiceId, @RequestBody @Valid Invoice invoice) {
        invoice.setInvoiceId(invoiceId);
        Invoice updatedInvoice = invoiceService.updateInvoice(invoice);
        if (updatedInvoice == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedInvoice);
    }

    @PatchMapping("/{invoiceId}")
    public ResponseEntity<Invoice> patchInvoice(@PathVariable String invoiceId, @RequestBody Map<String, Object> updates) {
        Invoice existingInvoice = invoiceService.getInvoiceById(invoiceId);
        if (existingInvoice == null) {
            return ResponseEntity.notFound().build();
        }
        invoiceService.patchInvoice(invoiceId, updates);
        Invoice updatedInvoice = invoiceService.getInvoiceById(invoiceId);
        return ResponseEntity.ok(updatedInvoice);
    }

    @DeleteMapping("/{invoiceId}")
    public ResponseEntity<String> deleteInvoice(@PathVariable String invoiceId) {
        invoiceService.deleteInvoice(invoiceId);
        return ResponseEntity.ok("Invoice deleted successfully!");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
