package com.ecommerce.order.repository;

import com.ecommerce.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    @Query("SELECT o.orderId FROM Order o WHERE o.userId = ?1")
    List<String> findOrderIdsByUserId(String userId);

    @Query("SELECT o FROM Order o JOIN FETCH o.orderItems WHERE o.userId = :userId")
    List<Order> findOrdersWithOrderItemsByUserId(@Param("userId") String userId);
}

