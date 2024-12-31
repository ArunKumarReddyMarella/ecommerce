package com.ecommerce.common.service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CsvExportService implements FileExportService{
    @Override
    public byte[] export(Set<String> selectedColumns, JsonNode jsonData) {
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.registerModule(new JavaTimeModule());
        csvMapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);

        CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder().setUseHeader(true);

        for (String column : selectedColumns) {
            csvSchemaBuilder.addColumn(column);
        }
        CsvSchema csvSchema = csvSchemaBuilder.build();

        try {
            return csvMapper.writer(csvSchema).writeValueAsBytes(jsonData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while exporting data to CSV", e);
        }
    }
}
