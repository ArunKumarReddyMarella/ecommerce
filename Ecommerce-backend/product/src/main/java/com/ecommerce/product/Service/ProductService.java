package com.ecommerce.product.Service;

import com.ecommerce.product.Entity.Product;
import com.ecommerce.product.bean.ProductExportBean;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface ProductService {
    public Page<Product> getProducts(Pageable pageable);

    public Product getProductById(String Id);

    public Product getProductByProductName(String productName);

    public Product createProduct(Product product);

    public void deleteProduct(String Id);

    public Product updateProduct(Product updatedProduct);

    public Product patchProduct(String productId, Map<String, Object> updates);

//    public byte[] exportProducts(String format, @Valid ProductExportBean productExportBean);
}
