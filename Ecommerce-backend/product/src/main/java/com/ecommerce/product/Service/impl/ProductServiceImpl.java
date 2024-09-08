package com.ecommerce.product.Service.impl;

import com.ecommerce.product.Entity.Product;
import com.ecommerce.product.Repository.ProductRepository;
import com.ecommerce.product.Service.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Product getProductById(String Id) {
        Optional<Product> optionalProduct = productRepository.findById(Id);
        return optionalProduct.orElse(null);
    }

    @Override
    public Product getProductByProductName(String productName){
        Optional<Product> optionalProduct = productRepository.findByProductName(productName);
//        return optionalProduct.orElse(null);
        if(optionalProduct.isEmpty())
            return null;
        return optionalProduct.get();
    }

    @Override
    public Product createProduct(Product product) {
        if(product.getProductId() == null)
            product.setProductId(UUID.randomUUID().toString());
        else {
            Optional<Product> existingProduct = productRepository.findById(product.getProductId());
            if (existingProduct.isPresent()) {
                throw new RuntimeException("Product with ID " + product.getProductId() + " already exists.");
            }
        }
        return productRepository.saveAndFlush(product);
    }


    @Override
    public void deleteProduct(String Id) {
        productRepository.deleteById(Id);
    }

    @Override
    public Product updateProduct(Product updatedProduct) {
        Product product = productRepository.saveAndFlush(updatedProduct);
        return product;
    }

    @Override
    public void patchProduct(String productId, Map<String, Object> updates) {
        Product existingProduct = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        updates.forEach((key, value) -> {
            try {
                // Use reflection or property accessors to update specific fields based on the key
                Field field = Product.class.getDeclaredField(key);
                field.setAccessible(true);
                if (field.getType() == Timestamp.class) {
                    try {
                        // Use OffsetDateTime to parse the ISO 8601 format
                        OffsetDateTime odt = OffsetDateTime.parse((String) value);
                        Timestamp timestampValue = Timestamp.from(odt.toInstant());
                        field.set(existingProduct, timestampValue);
                    } catch (DateTimeParseException e) {
                        throw new IllegalArgumentException("Invalid format for "+key+" TimeStamp field");
                    }
                } else {
                    // Set other field types as usual
                    field.set(existingProduct, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // Handle potential exceptions (e.g., invalid field name)
                throw new IllegalArgumentException("Invalid update field: " + key);
            }
        });

        productRepository.save(existingProduct);
    }

}
