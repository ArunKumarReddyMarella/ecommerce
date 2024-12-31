package com.ecommerce.common.service;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Set;

public interface FileExportService {
    byte[] export(Set<String> selectedColumns, JsonNode jsonData);
}
