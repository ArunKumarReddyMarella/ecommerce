package com.ecommerce.product.Controller;

import com.ecommerce.product.Entity.Product;
import com.ecommerce.product.Service.ProductService;
import com.ecommerce.product.bean.ProductExportBean;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Page<Product>> getProducts(@RequestParam(defaultValue = "0") int page,  // Default to page 0
                                                     @RequestParam(defaultValue = "10") int size, // Default to 10 items per page
                                                     @RequestParam(defaultValue = "desc") String sortDirection) {
        logger.debug("Fetching products with page: {}, size: {}, sortDirection: {}", page, size, sortDirection);
        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "retailPrice"); // Example: sorting by productName
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> products = productService.getProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("id/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        logger.debug("Fetching product with id: {}", id);
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("productName")
    public ResponseEntity<Product> getProductByName(@RequestParam("paramName") String productName) {
        Product product = productService.getProductByProductName(productName);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody @Valid Product product) {
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.ok(createdProduct);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable String productId, @RequestBody @Valid Product product) {
//        product.setProductId(productId); // Ensure ID matches path variable
        Product updatedProduct = productService.updateProduct(product);
        return ResponseEntity.ok(updatedProduct);
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<Product> patchProduct(@PathVariable String productId, @RequestBody Map<String, Object> updates) {
        Product updatedProduct = productService.patchProduct(productId, updates); // Delegate patching logic to service
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("product deleted successfully!.");
    }

//    @GetMapping("/export")
//    public ResponseEntity exportProducts(@RequestParam String format, @Valid ProductExportBean productExportBean) {
//        byte[] exportedFile = productService.exportProducts(format, productExportBean);
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.parseMediaType("text/csv"));
//        headers.setContentDispositionFormData("attachment", "products.csv");
//        return ResponseEntity.ok().headers(headers).body(exportedFile);
//
//    }

}
