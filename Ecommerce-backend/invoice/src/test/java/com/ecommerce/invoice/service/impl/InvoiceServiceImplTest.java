package com.ecommerce.invoice.service.impl;

import com.ecommerce.invoice.entity.Invoice;
import com.ecommerce.invoice.exception.InvoiceAlreadyExistsException;
import com.ecommerce.invoice.exception.InvoiceNotFoundException;
import com.ecommerce.invoice.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    // Helper Methods for Test Data
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
        invoices.add(createTestInvoice()); // Add more for realistic testing
        return invoices;
    }

    private static Page<Invoice> createTestInvoicePage(Pageable pageable) {
        return new PageImpl<>(createTestInvoiceList(), pageable, 2);
    }

    // Tests for getInvoices()
    @Test
    void testGetInvoices() {
        Pageable pageable = mock(Pageable.class);
        Page<Invoice> expectedPage = createTestInvoicePage(pageable);
        when(invoiceRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<Invoice> actualPage = invoiceService.getInvoices(pageable);

        assertEquals(expectedPage, actualPage);
        for (int i = 0; i < expectedPage.getContent().size(); i++) {
            assertInvoiceEquals(expectedPage.getContent().get(i), actualPage.getContent().get(i));
        }
        verify(invoiceRepository, times(1)).findAll(pageable);
    }

    // Tests for getInvoiceById()
    @Test
    void testGetInvoiceById_ExistingInvoice() {
        Invoice invoice = createTestInvoice();
        when(invoiceRepository.findById(invoice.getInvoiceId()))
                .thenReturn(Optional.of(invoice));

        Invoice foundInvoice = invoiceService.getInvoiceById(invoice.getInvoiceId());

        assertEquals(invoice, foundInvoice);
        assertInvoiceEquals(invoice, foundInvoice);
        verify(invoiceRepository, times(1)).findById(invoice.getInvoiceId());
    }

    @Test
    void testGetInvoiceById_NonExistingInvoice() {
        String invoiceId = "NON-EXISTENT-ID";
        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.empty());

        assertThrows(InvoiceNotFoundException.class,
                () -> invoiceService.getInvoiceById(invoiceId));

        verify(invoiceRepository, times(1)).findById(invoiceId);
    }

    // Tests for createInvoice()
    @Test
    void testCreateInvoice_NewInvoice() {
        Invoice newInvoice = createTestInvoice();
        newInvoice.setInvoiceId(null); // Service should generate ID
        when(invoiceRepository.saveAndFlush(any(Invoice.class))).thenReturn(newInvoice);

        Invoice createdInvoice = invoiceService.createInvoice(newInvoice);

        assertNotNull(createdInvoice.getInvoiceId());
        assertInvoiceEquals(newInvoice, createdInvoice);
        verify(invoiceRepository, times(1)).saveAndFlush(any(Invoice.class));
    }

    @Test
    void testCreateInvoice_AlreadyExists() {
        Invoice existingInvoice = createTestInvoice();
        when(invoiceRepository.findById(existingInvoice.getInvoiceId()))
                .thenReturn(Optional.of(existingInvoice));

        assertThrows(InvoiceAlreadyExistsException.class,
                () -> invoiceService.createInvoice(existingInvoice));

        verify(invoiceRepository, times(1)).findById(existingInvoice.getInvoiceId());
        verify(invoiceRepository, never()).saveAndFlush(any());
    }

    @Test
    void testCreateInvoice_Success() {
        Invoice newInvoice = createTestInvoice();

        when(invoiceRepository.findById(newInvoice.getInvoiceId())).thenReturn(Optional.empty());
        when(invoiceRepository.saveAndFlush(any(Invoice.class))).thenReturn(newInvoice);
        Invoice createdInvoice = invoiceService.createInvoice(newInvoice);

        assertNotNull(createdInvoice.getInvoiceId());
        assertInvoiceEquals(newInvoice, createdInvoice);
        verify(invoiceRepository, times(1)).findById(newInvoice.getInvoiceId());
        verify(invoiceRepository, times(1)).saveAndFlush(any(Invoice.class));
    }

    // Tests for updateInvoice()
    @Test
    void testUpdateInvoice_Successful() {
        Invoice updatedInvoice = createTestInvoice();
        when(invoiceRepository.existsById(updatedInvoice.getInvoiceId()))
                .thenReturn(true);
        when(invoiceRepository.saveAndFlush(any(Invoice.class)))
                .thenReturn(updatedInvoice);

        Invoice result = invoiceService.updateInvoice(updatedInvoice);

        assertEquals(updatedInvoice, result);
        assertInvoiceEquals(updatedInvoice, result);
        verify(invoiceRepository, times(1)).existsById(updatedInvoice.getInvoiceId());
        verify(invoiceRepository, times(1)).saveAndFlush(updatedInvoice);
    }

    @Test
    void testUpdateInvoice_NotFound() {
        Invoice nonExistingInvoice = createTestInvoice();
        when(invoiceRepository.existsById(nonExistingInvoice.getInvoiceId()))
                .thenReturn(false);

        assertThrows(InvoiceNotFoundException.class,
                () -> invoiceService.updateInvoice(nonExistingInvoice));

        verify(invoiceRepository, times(1)).existsById(nonExistingInvoice.getInvoiceId());
        verify(invoiceRepository, never()).saveAndFlush(any());
    }

    // Tests for patchInvoice()
    @Test
    void testPatchInvoice_Successful() {
        String invoiceId = "INV-001";
        Map<String, Object> updates = new HashMap<>();
        updates.put("paymentAmount", BigDecimal.valueOf(200.00));

        Invoice existingInvoice = createTestInvoice();
        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(existingInvoice));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        invoiceService.patchInvoice(invoiceId, updates);

        assertEquals(BigDecimal.valueOf(200.00), existingInvoice.getPaymentAmount());
        assertInvoiceEquals(existingInvoice, existingInvoice);
        verify(invoiceRepository, times(1)).findById(invoiceId);
        verify(invoiceRepository, times(1)).save(existingInvoice);
    }

    @Test
    void testPatchInvoice_NotFound() {
        String invoiceId = "NON-EXISTENT-ID";
        Map<String, Object> updates = new HashMap<>();
        updates.put("paymentAmount", BigDecimal.valueOf(200.00));

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.empty());

        assertThrows(InvoiceNotFoundException.class,
                () -> invoiceService.patchInvoice(invoiceId, updates));

        verify(invoiceRepository, times(1)).findById(invoiceId);
        verify(invoiceRepository, never()).save(any());
    }

    @Test
    void testPatchInvoice_InvalidField() {
        String invoiceId = "INV-001";
        Map<String, Object> updates = new HashMap<>();
        updates.put("invalidField", "invalidValue");

        Invoice existingInvoice = createTestInvoice();
        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(existingInvoice));

        assertThrows(IllegalArgumentException.class,
                () -> invoiceService.patchInvoice(invoiceId, updates));

        verify(invoiceRepository, times(1)).findById(invoiceId);
        verify(invoiceRepository, never()).save(any());
    }

    // Tests for deleteInvoice()
    @Test
    void testDeleteInvoice_Successful() {
        String invoiceId = "INV-001";
        when(invoiceRepository.existsById(invoiceId)).thenReturn(true);
        doNothing().when(invoiceRepository).deleteById(invoiceId); // No exception thrown

        invoiceService.deleteInvoice(invoiceId);

        verify(invoiceRepository, times(1)).existsById(invoiceId);
        verify(invoiceRepository, times(1)).deleteById(invoiceId);
    }

    @Test
    void testDeleteInvoice_NotFound() {
        String invoiceId = "NON-EXISTENT-ID";
        when(invoiceRepository.existsById(invoiceId)).thenReturn(false);

        assertThrows(InvoiceNotFoundException.class,
                () -> invoiceService.deleteInvoice(invoiceId));

        verify(invoiceRepository, times(1)).existsById(invoiceId);
        verify(invoiceRepository, never()).deleteById(anyString());
    }

    private void assertInvoiceEquals(Invoice expectedInvoice, Invoice actualInvoice) {
        assertEquals(expectedInvoice.getInvoiceId(), actualInvoice.getInvoiceId());
        assertEquals(expectedInvoice.getTransactionId(), actualInvoice.getTransactionId());
        assertEquals(expectedInvoice.getPaymentAmount(), actualInvoice.getPaymentAmount());
        assertEquals(expectedInvoice.getPaymentDate(), actualInvoice.getPaymentDate());
    }

}
