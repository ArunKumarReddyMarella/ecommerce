package com.ecommerce.invoice.controller;

import com.ecommerce.invoice.entity.Invoice;
import com.ecommerce.invoice.service.InvoiceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceControllerTest {

    @Mock
    private InvoiceService invoiceService;

    @InjectMocks
    private InvoiceController invoiceController;

    // Sample Data for Testing
    private static Invoice createTestInvoice() {
        Invoice invoice = new Invoice();
        invoice.setInvoiceId("INV-001");
        invoice.setTransactionId("TRANS-001");
        invoice.setPaymentAmount(BigDecimal.valueOf(100.50));
        invoice.setPaymentDate(Timestamp.valueOf(LocalDateTime.now()));
        return invoice;
    }

    private static List<Invoice> createTestInvoiceList() {
        List<Invoice> invoices = new ArrayList<>();
        invoices.add(createTestInvoice());
        invoices.add(createTestInvoice());
        return invoices;
    }

    private static Page<Invoice> createTestInvoicePage(Pageable pageable) {
        return new PageImpl<>(createTestInvoiceList(), pageable, 2);
    }

    // Tests for getInvoices()
    @Test
    void testGetInvoices_Success() {
        int page = 0;
        int size = 10;
        String sortDirection = "desc";
        Sort sort = Sort.by(Sort.Direction.DESC, "paymentDate");
        Pageable pageable = PageRequest.of(page, size,sort);
        Page<Invoice> expectedInvoices = createTestInvoicePage(pageable);

        when(invoiceService.getInvoices(pageable)).thenReturn(expectedInvoices);

        ResponseEntity<Page<Invoice>> response = invoiceController.getInvoices(page, size, sortDirection);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedInvoices, response.getBody());
        for (int i = 0; i < expectedInvoices.getContent().size(); i++) {
            assertInvoiceEquals(expectedInvoices.getContent().get(i), response.getBody().getContent().get(i));
        }
        verify(invoiceService, times(1)).getInvoices(pageable);
    }

    @Test
    void testGetInvoices_asc() {
        int page = 0;
        int size = 10;
        String sortDirection = "asc";
        Sort sort = Sort.by(Sort.Direction.ASC, "paymentDate");
        Pageable pageable = PageRequest.of(page, size,sort);
        Page<Invoice> expectedInvoices = createTestInvoicePage(pageable);

        when(invoiceService.getInvoices(pageable)).thenReturn(expectedInvoices);

        ResponseEntity<Page<Invoice>> response = invoiceController.getInvoices(page, size, sortDirection);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedInvoices, response.getBody());
        for (int i = 0; i < expectedInvoices.getContent().size(); i++) {
            assertInvoiceEquals(expectedInvoices.getContent().get(i), response.getBody().getContent().get(i));
        }
        verify(invoiceService, times(1)).getInvoices(pageable);
    }

    // Tests for getInvoiceById()
    @Test
    void testGetInvoiceById_Success() {
        String invoiceId = "INV-001";
        Invoice expectedInvoice = createTestInvoice();
        when(invoiceService.getInvoiceById(invoiceId)).thenReturn(expectedInvoice);

        ResponseEntity<Invoice> response = invoiceController.getInvoiceById(invoiceId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedInvoice, response.getBody());
        assertInvoiceEquals(expectedInvoice, response.getBody());
        verify(invoiceService, times(1)).getInvoiceById(invoiceId);
    }


    // Tests for createInvoice()
    @Test
    void testCreateInvoice_Success() {
        Invoice newInvoice = createTestInvoice();
        when(invoiceService.createInvoice(newInvoice)).thenReturn(newInvoice);

        ResponseEntity<Invoice> response = invoiceController.createInvoice(newInvoice);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newInvoice, response.getBody());
        assertInvoiceEquals(newInvoice, response.getBody());
        verify(invoiceService, times(1)).createInvoice(newInvoice);
    }

    // Tests for updateInvoice()
    @Test
    void testUpdateInvoice_Success() {
        String invoiceId = "INV-001";
        Invoice updatedInvoice = createTestInvoice();
        when(invoiceService.updateInvoice(updatedInvoice)).thenReturn(updatedInvoice);

        ResponseEntity<Invoice> response = invoiceController.updateInvoice(invoiceId, updatedInvoice);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedInvoice, response.getBody());
        assertInvoiceEquals(updatedInvoice, response.getBody());
        verify(invoiceService, times(1)).updateInvoice(updatedInvoice);
    }

    @Test
    void testUpdateInvoice_NotFound() {
        String invoiceId = "INV-001";
        Invoice updatedInvoice = createTestInvoice();
        when(invoiceService.updateInvoice(updatedInvoice)).thenReturn(null);

        ResponseEntity<Invoice> response = invoiceController.updateInvoice(invoiceId, updatedInvoice);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(invoiceService, times(1)).updateInvoice(updatedInvoice);
    }

    // Tests for patchInvoice()
    @Test
    void testPatchInvoice_Success() {
        String invoiceId = "INV-001";
        Map<String, Object> updates = Map.of("paymentAmount", BigDecimal.valueOf(150.00));
        Invoice originalInvoice = createTestInvoice();
        Invoice patchedInvoice = createTestInvoice();
        patchedInvoice.setPaymentAmount(BigDecimal.valueOf(150.00)); // Reflect the patch

        when(invoiceService.getInvoiceById(invoiceId)).thenReturn(originalInvoice);
        when(invoiceService.patchInvoice(invoiceId, updates)).thenReturn(patchedInvoice);

        ResponseEntity<Invoice> response = invoiceController.patchInvoice(invoiceId, updates);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(patchedInvoice, response.getBody());
        assertInvoiceEquals(patchedInvoice, Objects.requireNonNull(response.getBody()));
        verify(invoiceService, times(1)).getInvoiceById(invoiceId);
        verify(invoiceService, times(1)).patchInvoice(invoiceId, updates);
    }

    @Test
    void testPatchInvoice_NotFound() {
        String invoiceId = "INV-001";
        Map<String, Object> updates = Map.of("paymentAmount", BigDecimal.valueOf(150.00));
        when(invoiceService.getInvoiceById(invoiceId)).thenReturn(null);

        ResponseEntity<Invoice> response = invoiceController.patchInvoice(invoiceId, updates);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(invoiceService, times(1)).getInvoiceById(invoiceId);
        verify(invoiceService, never()).patchInvoice(invoiceId, updates);
    }

    // Tests for deleteInvoice()
    @Test
    void testDeleteInvoice_Success() {
        String invoiceId = "INV-001";

        ResponseEntity<String> response = invoiceController.deleteInvoice(invoiceId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Invoice deleted successfully!", response.getBody());
        verify(invoiceService, times(1)).deleteInvoice(invoiceId);
    }

    private void assertInvoiceEquals(Invoice expectedInvoice, Invoice actualInvoice) {
        assertEquals(expectedInvoice.getInvoiceId(), actualInvoice.getInvoiceId());
        assertEquals(expectedInvoice.getTransactionId(), actualInvoice.getTransactionId());
        assertEquals(expectedInvoice.getPaymentAmount(), actualInvoice.getPaymentAmount());
        assertEquals(expectedInvoice.getPaymentDate(), actualInvoice.getPaymentDate());
    }
}

