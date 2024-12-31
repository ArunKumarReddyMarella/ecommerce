package com.ecommerce.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ExportService {

    public byte[] exportProduct(String format, Set<String> selectedColumns, String exportData) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        JsonNode jsonData;
        try {
            jsonData = objectMapper.readTree(exportData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        ExportType exportType = ExportType.valueOf(format.toUpperCase());
        FileExportService fileExportService = exportType.getService();
        return fileExportService.export(selectedColumns, jsonData);
    }
}
