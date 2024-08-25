package com.microservice.product.Service;

import com.microservice.product.Entity.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public interface ProductService {
    public List<Product> getProducts();
    public Product getProductById(String Id);
    public Product createProduct(Product product);
    public void deleteProduct(String Id);

    public Product updateProduct(Product updatedProduct);
    public void patchProduct(String productId, Map<String, Object> updates);
}
