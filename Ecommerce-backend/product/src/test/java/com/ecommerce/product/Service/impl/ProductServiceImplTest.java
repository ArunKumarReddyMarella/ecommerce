package com.ecommerce.product.Service.impl;

import com.ecommerce.product.Entity.Product;
import com.ecommerce.product.Repository.ProductRepository;
import com.ecommerce.product.exception.ProductAlreadyExistsException;
import com.ecommerce.product.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private static List<Product> getProducts() {
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
        product2.setRetailPrice(BigDecimal.valueOf(15));
        product2.setDiscountedPrice(BigDecimal.valueOf(8));
        product2.setImageUrls("https://example.com/product2.jpg");
        product2.setFkAdvantageProduct(true);
        product2.setProductDescription("Description 2");
        product2.setProductRating("3.5");
        product2.setOverallRating("4.2");
        product2.setBrand("Brand B");
        product2.setProductSpecifications("Spec B");
        product2.setStockQuantity(5);
        product2.setQuantityUnit("units");
        product2.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        productList.add(product2);
        return productList;
    }

    private static Page<Product> getProducts(Pageable pageable) {
        List<Product> productList = getProducts();
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
    void testGetProducts() {
        // Prepare test data
        Pageable pageable = mock(Pageable.class);
        Page<Product> expectedProducts = getProducts(pageable);

        // Mock repository method
        when(productRepository.findAll(pageable)).thenReturn(expectedProducts);

        // Call the service method
        Page<Product> actualProducts = productService.getProducts(pageable);

        // Verify the results
        for(int i = 0; i < expectedProducts.getContent().size(); i++) {
            assertProductFields(expectedProducts.getContent().get(i), actualProducts.getContent().get(i));
        }
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetProductById_ExistingId() {
        // Prepare test data
        String productId = "1";
        Product product = getProduct();
        // Mock repository method
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Call the service method
        Product actualProduct = productService.getProductById(productId);

        // Verify the results
        assertProductFields(product, actualProduct);

        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void testGetProductById_NonExistingId() {
        // Prepare test data
        String productId = "1";

        // Mock repository method
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Call the service method and assert exception
        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(productId));

        // Verify the results
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void testGetProductByProductName_ExistingName() {
        String productName = "Product 1";
        Product product = getProduct();
        when(productRepository.findByProductName(productName)).thenReturn(Optional.of(product));

        Product actualProduct = productService.getProductByProductName(productName);

        assertProductFields(product, actualProduct);
        verify(productRepository, times(1)).findByProductName(productName);
    }

    @Test
    void testGetProductByProductName_NonExistingName() {
        String productName = "Non Existing Product";
        when(productRepository.findByProductName(any(String.class))).thenThrow(ProductNotFoundException.class);

        assertThrows(ProductNotFoundException.class, () -> productService.getProductByProductName(productName));

        verify(productRepository, times(1)).findByProductName(productName);
    }

    @Test
    void testCreateProduct_Successful() {
        // Prepare test data
        Product product = getProduct();
        product.setProductId(null);
        // Mock repository method
        when(productRepository.saveAndFlush(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Call the service method
        Product createdProduct = productService.createProduct(product);

        // Verify the results
        assertNotNull(createdProduct.getProductId());
        verify(productRepository, times(1)).saveAndFlush(any(Product.class));
    }

    @Test
    void testCreateNewProduct_Successful() {
        // Prepare test data
        Product product = getProduct();
        // Mock repository method
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.empty());
        when(productRepository.saveAndFlush(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Call the service method
        Product createdProduct = productService.createProduct(product);

        // Verify the results
        assertNotNull(createdProduct.getProductId());
        verify(productRepository, times(1)).saveAndFlush(any(Product.class));
    }

    @Test
    void testCreateProduct_ProductAlreadyExists() {
        // Prepare test data
        Product product = getProduct();
        // Mock repository method
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));

        // Call the service method and assert exception
        assertThrows(ProductAlreadyExistsException.class, () -> productService.createProduct(product));

        // Verify the results
        verify(productRepository, times(1)).findById(product.getProductId());
        verify(productRepository, never()).saveAndFlush(any(Product.class));
    }

    @Test
    void testDeleteProduct_ExistingId() {
        // Prepare test data
        String productId = "1";

        // Mock repository method
        when(productRepository.existsById(productId)).thenReturn(true);
        doNothing().when(productRepository).deleteById(productId);

        // Call the service method
        productService.deleteProduct(productId);

        // Verify the results
        verify(productRepository, times(1)).existsById(productId);
        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    void testDeleteProduct_NonExistingId() {
        // Prepare test data
        String productId = "1";

        // Mock repository method
        when(productRepository.existsById(productId)).thenReturn(false);

        // Call the service method and assert exception
        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(productId));

        // Verify the results
        verify(productRepository, times(1)).existsById(productId);
        verify(productRepository, never()).deleteById(anyString());
    }

    @Test
    void testUpdateProduct_Successful() {
        // Prepare test data
        String productId = "1";
        Product updatedProduct = getUpdatedProduct();

        // Mock repository method
        when(productRepository.existsById(productId)).thenReturn(true);
//        when(productRepository.saveAndFlush(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productRepository.saveAndFlush(any(Product.class))).thenReturn(updatedProduct);
        // Call the service method
        Product result = productService.updateProduct(updatedProduct);

        // Verify the results
        assertProductFields(updatedProduct, result);
        verify(productRepository, times(1)).existsById(productId);
        verify(productRepository, times(1)).saveAndFlush(updatedProduct);
    }

    @Test
    void testUpdateProduct_ProductNotFound() {
        // Prepare test data
        String productId = "1";
        Product updatedProduct = getUpdatedProduct();

        // Mock repository method
        when(productRepository.existsById(productId)).thenReturn(false);

        // Call the service method and assert exception
        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(updatedProduct));

        // Verify the results
        verify(productRepository, times(1)).existsById(productId);
        verify(productRepository, never()).saveAndFlush(any(Product.class));
    }

    private static Product getUpdatedProduct() {
        Product updatedProduct = new Product();
        updatedProduct.setProductId("1");
        updatedProduct.setProductName("Updated Product");
        updatedProduct.setProductDescription("Updated Description");
        return updatedProduct;
    }

    @Test
    void testPatchProduct_Successful() {
        // Prepare test data
        String productId = "1";
        Product existingProduct = getProduct();
        Map<String, Object> updates = Map.of("productName", "New Product Name");

        // Mock repository method
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Call the service method
        Product patchedProduct = productService.patchProduct(productId, updates);

        // Verify the results
        assertEquals("New Product Name", patchedProduct.getProductName());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(existingProduct);
    }

    @Test
    void testPatchProduct_ProductNotFound() {
        // Prepare test data
        String productId = "1";
        Map<String, Object> updates = Map.of("productName", "New Product Name");

        // Mock repository method
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Call the service method and assert exception
        assertThrows(ProductNotFoundException.class, () -> productService.patchProduct(productId, updates));

        // Verify the results
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testPatchProduct_InvalidData() {
        // Prepare test data
        String productId = "1";
        Product existingProduct = getProduct();
        Map<String, Object> updates = Map.of("invalidField", "Invalid Value");

        // Mock repository method
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        // Call the service method and assert exception
        assertThrows(IllegalArgumentException.class, () -> productService.patchProduct(productId, updates));

        // Verify the results
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
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
