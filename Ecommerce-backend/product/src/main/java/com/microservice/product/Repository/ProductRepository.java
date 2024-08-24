package com.microservice.product.Repository;

import com.microservice.product.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("SELECT MAX(p.productId) FROM Product p")
    int findMaxProductId();

    Product deleteById(int Id);
}
