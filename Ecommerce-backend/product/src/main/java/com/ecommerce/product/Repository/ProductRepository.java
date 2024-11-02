package com.ecommerce.product.Repository;

import com.ecommerce.product.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {

//    @Query("select p from Product p where p.productName = :productName")
//    Optional<Product> findByProductName(@Param("productName") String productName);

    Optional<Product> findByProductName(String productName);
}
