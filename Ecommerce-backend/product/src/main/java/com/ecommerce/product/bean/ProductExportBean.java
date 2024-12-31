package com.ecommerce.product.bean;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class ProductExportBean {

    @NotEmpty(message = "Atleast one product ID is required")
    private Set<String> productIDs;

    @NotEmpty(message = "Atleast one column is required")
    private Set<String> selectedColumns;
}
