package com.ecommerce.product.Controller;

import com.ecommerce.product.Entity.Product;
import com.ecommerce.product.Service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private static Page<Product> getProducts(Pageable pageable) {
        List<Product> productList = new ArrayList<>();
        Product product1 = new Product();
        product1.setProductId("1");
        product1.setCrawlTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        product1.setProductUrl("https://example.com/product1");
        product1.setProductName("Product 1");
        product1.setCategories("Category 1");
        product1.setPid("P001");
        product1.setRetailPrice(BigDecimal.TEN);
        product1.setDiscountedPrice(BigDecimal.ONE);
        product1.setImageUrls("https://example.com/product1.jpg");
        product1.setFkAdvantageProduct(false);
        product1.setProductDescription("Description 1");
        product1.setProductRating("4.5");
        product1.setOverallRating("4.0");
        product1.setBrand("Brand A");
        product1.setProductSpecifications("Spec A");
        product1.setStockQuantity(10);
        product1.setQuantityUnit("pcs");
        product1.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        productList.add(product1);
        Product product2 = new Product();
        product2.setProductId("2");
        product2.setCrawlTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        product2.setProductUrl("https://example.com/product2");
        product2.setProductName("Product 2");
        product2.setCategories("Category 2");
        product2.setPid("P002");
        product2.setRetailPrice(BigDecimal.TEN);
        product2.setDiscountedPrice(BigDecimal.ONE);
        product2.setImageUrls("https://example.com/product2.jpg");
        product2.setFkAdvantageProduct(true);
        product2.setProductDescription("Description 2");
        product2.setProductRating("3.5");
        product2.setOverallRating("4.2");
        product2.setBrand("Brand B");
        product2.setProductSpecifications("Spec B");
        product2.setStockQuantity(10);
        product2.setQuantityUnit("pcs");
        product2.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        productList.add(product2);
        return new PageImpl<>(productList, pageable, productList.size());
    }

    private static Product getProduct() {
        Product product = new Product();
        product.setProductId("1");
        product.setCrawlTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        product.setProductUrl("https://example.com/product1");
        product.setProductName("Product 1");
        product.setCategories("Category 1");
        product.setPid("P001");
        product.setRetailPrice(BigDecimal.TEN);
        product.setDiscountedPrice(BigDecimal.ONE);
        product.setImageUrls("https://example.com/product1.jpg");
        product.setFkAdvantageProduct(false);
        product.setProductDescription("Description 1");
        product.setProductRating("4.5");
        product.setOverallRating("4.0");
        product.setBrand("Brand A");
        product.setProductSpecifications("Spec A");
        product.setStockQuantity(10);
        product.setQuantityUnit("pcs");
        product.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        return product;
    }

    @Test
    void testGetProductsDesc() {
        // Prepare test data
        int page = 0;
        int size = 10;
        String sortDirection = "desc";
        String sortField = "retailPrice";
        Sort sort = Sort.by(Sort.Direction.DESC, sortField);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> expectedProducts = getProducts(pageable);

        // Mock service method
        when(productService.getProducts(pageable)).thenReturn(expectedProducts);

        // Call the controller method
        ResponseEntity<Page<Product>> response = productController.getProducts(page, size, sortDirection, sortField);

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
        String sortField = "retailPrice";
        Sort sort = Sort.by(Sort.Direction.ASC, sortField);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> expectedProducts = getProducts(pageable);

        // Mock service method
        when(productService.getProducts(pageable)).thenReturn(expectedProducts);

        // Call the controller method
        ResponseEntity<Page<Product>> response = productController.getProducts(page, size, sortDirection, sortField);

        // Verify the results
        assertEquals(HttpStatus.OK, response.getStatusCode());
        for(int i = 0; i < expectedProducts.getContent().size(); i++) {
            assertProductFields(expectedProducts.getContent().get(i), Objects.requireNonNull(response.getBody()).getContent().get(i));
        }
        verify(productService, times(1)).getProducts(pageable);
    }

    @Test
    void testGetProductById() {
        String productId = UUID.randomUUID().toString();
        Product product = getProduct();
        when(productService.getProductById(productId)).thenReturn(product);

        ResponseEntity<Product> response = productController.getProductById(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertProductFields(product, Objects.requireNonNull(response.getBody()));
        verify(productService, times(1)).getProductById(productId);
    }

    @Test
    void testGetProductByName() {
        String productName = "Product 1";
        Product product = getProduct();

        when(productService.getProductByProductName(productName)).thenReturn(product);

        ResponseEntity<Product> response = productController.getProductByName(productName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertProductFields(product, Objects.requireNonNull(response.getBody()));
        verify(productService, times(1)).getProductByProductName(productName);
    }

    @Test
    void testCreateProduct() {
        Product product = getProduct();
        when(productService.createProduct(product)).thenReturn(product);

        ResponseEntity<Product> response = productController.createProduct(product);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertProductFields(product, Objects.requireNonNull(response.getBody()));
        verify(productService, times(1)).createProduct(product);
    }


    @Test
    void testUpdateProduct() {
        String productId = UUID.randomUUID().toString();
        Product product = getProduct();
        when(productService.updateProduct(product)).thenReturn(product);

        ResponseEntity<Product> response = productController.updateProduct(productId, product);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertProductFields(product, Objects.requireNonNull(response.getBody()));
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
        assertProductFields(updatedProduct, Objects.requireNonNull(response.getBody()));
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

    private void assertProductFields(Product expected, Product actual) {
        assertEquals(expected.getProductId(), actual.getProductId());
        assertEquals(expected.getCrawlTimestamp(), actual.getCrawlTimestamp());
        assertEquals(expected.getProductUrl(), actual.getProductUrl());
        assertEquals(expected.getProductName(), actual.getProductName());
        assertEquals(expected.getCategories(), actual.getCategories());
        assertEquals(expected.getPid(), actual.getPid());
        assertEquals(expected.getRetailPrice(), actual.getRetailPrice());
        assertEquals(expected.getDiscountedPrice(), actual.getDiscountedPrice());
        assertEquals(expected.getImageUrls(), actual.getImageUrls());
        assertEquals(expected.isFkAdvantageProduct(), actual.isFkAdvantageProduct());
        assertEquals(expected.getProductDescription(), actual.getProductDescription());
        assertEquals(expected.getProductRating(), actual.getProductRating());
        assertEquals(expected.getOverallRating(), actual.getOverallRating());
        assertEquals(expected.getBrand(), actual.getBrand());
        assertEquals(expected.getProductSpecifications(), actual.getProductSpecifications());
        assertEquals(expected.getStockQuantity(), actual.getStockQuantity());
        assertEquals(expected.getQuantityUnit(), actual.getQuantityUnit());
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
    }
}
