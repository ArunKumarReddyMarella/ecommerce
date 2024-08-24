package com.microservice.product.Service.impl;

import com.microservice.product.Entity.Product;
import com.microservice.product.Repository.ProductRepository;
import com.microservice.product.Service.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(int Id) {
        Optional<Product> optionalProduct = productRepository.findById(Id);
        return optionalProduct.orElse(null);
    }

    @Override
    public Product createProduct(Product product) {
        return productRepository.saveAndFlush(product);
    }

    @Override
    public int getMaxId() {
        return productRepository.findMaxProductId();
    }

    @Override
    public Product deleteProduct(int Id) {
        Product product = productRepository.deleteById(Id);
        return product;
    }

    @Override
    public Product updateProduct(Product updatedProduct) {
        Product product = productRepository.saveAndFlush(updatedProduct);
        return product;
    }

    @Override
    public void patchProduct(int productId, Map<String, Object> updates) {
        Product existingProduct = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        updates.forEach((key, value) -> {
            try {
                // Use reflection or property accessors to update specific fields based on the key
                Field field = Product.class.getDeclaredField(key);
                field.setAccessible(true);
                field.set(existingProduct, value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // Handle potential exceptions (e.g., invalid field name)
                throw new IllegalArgumentException("Invalid update field: " + key);
            }
        });

        productRepository.save(existingProduct);
    }

}
