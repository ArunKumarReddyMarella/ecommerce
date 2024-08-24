package com.microservice.product.Controller;

import com.microservice.product.Entity.Product;
import com.microservice.product.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getProducts(){
        List<Product> products = productService.getProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("maxId")
    public ResponseEntity<Integer> getMaxId(){
        return ResponseEntity.ok(productService.getMaxId());
    }

    @GetMapping("{id}")
    public ResponseEntity<Product> getProduct(@PathVariable int id){
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product){
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.ok(createdProduct);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable int productId, @RequestBody Product product) {
//        product.setProductId(productId); // Ensure ID matches path variable
        Product updatedProduct = productService.updateProduct(product);
        if (updatedProduct == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedProduct);
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<Product> patchProduct(@PathVariable int productId, @RequestBody Map<String, Object> updates) {
        Product existingProduct = productService.getProductById(productId);
        if (existingProduct == null) {
            return ResponseEntity.notFound().build();
        }
        productService.patchProduct(productId, updates); // Delegate patching logic to service
        Product updatedProduct = productService.getProductById(productId); // Refetch after patching
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable int id){
        Product deletedProduct = productService.deleteProduct(id);
        return ResponseEntity.ok("Todo deleted successfully!.");
    }

}
