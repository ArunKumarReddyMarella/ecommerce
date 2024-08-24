package com.microservice.product.Service;

import com.microservice.product.Entity.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public interface ProductService {
    public List<Product> getProducts();
    public Product getProductById(int Id);
    public Product createProduct(Product product);
    public int getMaxId(); //call lo vuna
    public Product deleteProduct(int Id);

    public Product updateProduct(Product updatedProduct);
    public void patchProduct(int productId, Map<String, Object> updates);
}
