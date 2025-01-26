package com.ecommerce.product.Service.impl;

import com.ecommerce.product.Entity.Product;
import com.ecommerce.product.Repository.ProductRepository;
import com.ecommerce.product.exception.ProductAlreadyExistsException;
import com.ecommerce.product.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private static List<Product> getProducts() {
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
        return productList;
    }

    private static Page<Product> getProducts(Pageable pageable) {
        List<Product> productList = getProducts();
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
    void testGetProducts() {
        // Prepare test data
        Pageable pageable = mock(Pageable.class);
        Page<Product> expectedProducts = getProducts(pageable);

        // Mock repository method
        when(productRepository.findAll(pageable)).thenReturn(expectedProducts);

        // Call the service method
        Page<Product> actualProducts = productService.getProducts(pageable);

        // Verify the results
        assertEquals(expectedProducts, actualProducts);
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
        assertEquals(product, actualProduct);
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

        assertEquals(product, actualProduct);
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
        when(productRepository.saveAndFlush(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Call the service method
        Product result = productService.updateProduct(updatedProduct);

        // Verify the results
        assertEquals(updatedProduct, result);
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
}
