package com.ecommerce.product.Controller;

import com.ecommerce.product.Entity.Product;
import com.ecommerce.product.Service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetProductsDesc() {
        // Prepare test data
        int page = 0;
        int size = 10;
        String sortDirection = "desc";
        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "retailPrice");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> expectedProducts = getProducts(pageable);

        // Mock service method
        when(productService.getProducts(pageable)).thenReturn(expectedProducts);

        // Call the controller method
        ResponseEntity<Page<Product>> response = productController.getProducts(page, size, sortDirection);

        // Verify the results
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedProducts, response.getBody());
        verify(productService, times(1)).getProducts(pageable);
    }

    @Test
    void testGetProductsAsc() {
        // Prepare test data
        int page = 0;
        int size = 10;
        String sortDirection = "asc";
        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "retailPrice");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> expectedProducts = getProducts(pageable);

        // Mock service method
        when(productService.getProducts(pageable)).thenReturn(expectedProducts);

        // Call the controller method
        ResponseEntity<Page<Product>> response = productController.getProducts(page, size, sortDirection);

        // Verify the results
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedProducts, response.getBody());
        verify(productService, times(1)).getProducts(pageable);
    }

    private static Page<Product> getProducts(Pageable pageable) {
        List<Product> productList = new ArrayList<>();
        Product product1 = new Product();
        product1.setProductId("1");
        product1.setProductName("Product 1");
        product1.setProductDescription("Description 1");
        product1.setRetailPrice(BigDecimal.TEN);
        product1.setDiscountedPrice(BigDecimal.ONE);
        product1.setStockQuantity(10);
        product1.setQuantityUnit("pcs");
        productList.add(product1);
        Product product2 = new Product();
        product2.setProductId("2");
        product2.setProductName("Product 2");
        product2.setProductDescription("Description 2");
        product2.setRetailPrice(BigDecimal.TEN);
        product2.setDiscountedPrice(BigDecimal.ONE);
        product2.setStockQuantity(10);
        product2.setQuantityUnit("pcs");
        productList.add(product2);
        Page<Product> expectedProducts = new PageImpl<>(productList, pageable, productList.size());
        return expectedProducts;
    }

    private static Product getProduct() {
        Product product = new Product();
        product.setProductId("1");
        product.setProductName("Product 1");
        product.setProductDescription("Description 1");
        product.setRetailPrice(BigDecimal.TEN);
        product.setDiscountedPrice(BigDecimal.ONE);
        product.setStockQuantity(10);
        product.setQuantityUnit("pcs");
        return product;
    }

    @Test
    void testGetProductById() {
        String productId = UUID.randomUUID().toString();
        Product product = getProduct();
        when(productService.getProductById(productId)).thenReturn(product);

        ResponseEntity<Product> response = productController.getProductById(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(product, response.getBody());
        verify(productService, times(1)).getProductById(productId);
    }

    @Test
    void testGetProductByName() {
        String productName = "Product 1";
        Product product = getProduct();

        when(productService.getProductByProductName(productName)).thenReturn(product);

        ResponseEntity<Product> response = productController.getProductByName(productName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(product, response.getBody());
        verify(productService, times(1)).getProductByProductName(productName);
    }

    @Test
    void testCreateProduct() {
        Product product = getProduct();
        when(productService.createProduct(product)).thenReturn(product);

        ResponseEntity<Product> response = productController.createProduct(product);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(product, response.getBody());
        verify(productService, times(1)).createProduct(product);
    }


    @Test
    void testUpdateProduct() {
        String productId = UUID.randomUUID().toString();
        Product product = getProduct();
        when(productService.updateProduct(product)).thenReturn(product);

        ResponseEntity<Product> response = productController.updateProduct(productId, product);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(product, response.getBody());
        verify(productService, times(1)).updateProduct(product);
    }

    @Test
    void testPatchProduct() {
        String productId = "1";
        Map<String, Object> updates = Map.of("productName", "New Name");
        Product updatedProduct = getProduct();
        when(productService.patchProduct(productId, updates)).thenReturn(updatedProduct);

        ResponseEntity<Product> response = productController.patchProduct(productId, updates);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedProduct, response.getBody());
        verify(productService, times(1)).patchProduct(productId, updates);
    }

    @Test
    void testDeleteProduct() {
        String productId = UUID.randomUUID().toString();

        ResponseEntity<String> response = productController.deleteProduct(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("product deleted successfully!.", response.getBody());
        verify(productService, times(1)).deleteProduct(productId);
    }
}
