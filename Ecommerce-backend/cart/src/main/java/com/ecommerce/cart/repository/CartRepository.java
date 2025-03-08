package com.ecommerce.cart.repository;

import com.ecommerce.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart,String> {

    List<Cart> findByUserId(String userId);
}
