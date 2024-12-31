package com.ecommerce.common.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PdfExportService implements FileExportService{
    @Override
    public byte[] export(Set<String> selectedColumns, JsonNode jsonData) {
        return new byte[0];
    }
}
