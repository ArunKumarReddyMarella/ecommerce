package com.microservice.product.Repository;

import com.microservice.product.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

//    @Query("select p from Product p where p.productName = :productName")
//    Optional<Product> findByProductName(@Param("productName") String productName);

    Optional<Product> findByProductName(String productName);
}
