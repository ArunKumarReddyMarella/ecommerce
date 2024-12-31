package com.ecommerce.product.Service.impl;

//import com.ecommerce.common.service.ExportService;

import com.ecommerce.product.Entity.Product;
import com.ecommerce.product.Repository.ProductRepository;
import com.ecommerce.product.Service.ProductService;
import com.ecommerce.product.exception.ProductAlreadyExistsException;
import com.ecommerce.product.exception.ProductNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

//    @Autowired
//    private ExportService exportService;

    @Override
    public Page<Product> getProducts(Pageable pageable) {
        logger.debug("Fetching products with pageable: {}", pageable);
        return productRepository.findAll(pageable);
    }

    @Override
    public Product getProductById(String Id) {
        logger.debug("Fetching product with ID: {}", Id);
        Product product = productRepository.findById(Id).orElseThrow(() -> new ProductNotFoundException("Product with ID " + Id + " not found"));
        logger.debug("Product fetched: {}", product);
        return product;
    }

    @Override
    public Product getProductByProductName(String productName) {
        logger.debug("Fetching product with productName: {}", productName);
        Optional<Product> optionalProduct = productRepository.findByProductName(productName);
        return optionalProduct.orElse(null);
    }

    @Override
    public Product createProduct(Product product) {
        logger.debug("Creating product: {}", product);
        if (product.getProductId() == null)
            product.setProductId(UUID.randomUUID().toString());
        else {
            Optional<Product> existingProduct = productRepository.findById(product.getProductId());
            if (existingProduct.isPresent()) {
                logger.error("Product with ID {} already exists.", product.getProductId());
                throw new ProductAlreadyExistsException("Product with ID " + product.getProductId() + " already exists.");
            }
        }
        logger.debug("Product created: {}", product);
        return productRepository.saveAndFlush(product);
    }


    @Override
    public void deleteProduct(String Id) {
        if (!productRepository.existsById(Id)) {
            logger.error("Product with ID {} not found.", Id);
            throw new ProductNotFoundException("Product with ID " + Id + " not found.");
        }
        logger.debug("Deleting product with ID: {}", Id);
        productRepository.deleteById(Id);
    }

    @Override
    public Product updateProduct(Product updatedProduct) {
        if (!productRepository.existsById(updatedProduct.getProductId())) {
            logger.error("Product with ID {} not found.", updatedProduct.getProductId());
            throw new ProductNotFoundException("Product with ID " + updatedProduct.getProductId() + " not found.");
        }
        logger.debug("Updating product: {}", updatedProduct);
        return productRepository.saveAndFlush(updatedProduct);
    }

    @Override
    public Product patchProduct(String productId, Map<String, Object> updates) {
        logger.debug("Patching product with ID: {} with updates: {}", productId, updates);
        Product existingProduct = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product with ID " + productId + " not found."));

//        updates.forEach((key, value) -> {
//            try {
//                // Use reflection or property accessors to update specific fields based on the key
//                Field field = Product.class.getDeclaredField(key);
//                field.setAccessible(true);
//                if (field.getType() == Timestamp.class) {
//                    try {
//                        // Use OffsetDateTime to parse the ISO 8601 format
//                        OffsetDateTime odt = OffsetDateTime.parse((String) value);
//                        Timestamp timestampValue = Timestamp.from(odt.toInstant());
//                        field.set(existingProduct, timestampValue);
//                    } catch (DateTimeParseException e) {
//                        throw new IllegalArgumentException("Invalid format for "+key+" TimeStamp field");
//                    }
//                } else {
//                    // Set other field types as usual
//                    field.set(existingProduct, value);
//                }
//            } catch (NoSuchFieldException | IllegalAccessException e) {
//                // Handle potential exceptions (e.g., invalid field name)
//                throw new IllegalArgumentException("Invalid update field: " + key);
//            }
//        });

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // for Timestamp support

        try {
            // assuming updates is a Map<String, Object>
            mapper.updateValue(existingProduct, updates);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid update field: " + updates);
        }

        return productRepository.save(existingProduct);
    }

//    @Override
//    public byte[] exportProducts(String format, ProductExportBean productExportBean) {
//        Specification<Product> specification = new ProductSpecification(productExportBean.getProductIDs(), productExportBean.getSelectedColumns());
//        List<Product> products = productRepository.findAll(specification);
//        ObjectMapper objectMapper = new ObjectMapper();
//        String productsData = null;
//        try {
//            productsData = objectMapper.writeValueAsString(products);
//        }catch (JsonProcessingException e){
//            throw new RuntimeException("Failed to convert products to JSON", e);
//        }
//
//        return exportService.exportProduct(format,productExportBean.getSelectedColumns(),productsData);
//    }


}
