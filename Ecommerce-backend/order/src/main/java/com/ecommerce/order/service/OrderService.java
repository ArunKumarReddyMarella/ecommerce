package com.ecommerce.order.service;

import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface OrderService {
    Page<Order> getOrders(Pageable pageable);
    Order getOrderById(String id);
    Order createOrder(Order order);
    Order updateOrder(Order updatedOrder);
    void patchOrder(String orderId, Map<String, Object> updates);
    void deleteOrder(String id);


    Page<OrderItem> getOrderItemsByUserId(String userId, Pageable pageable);
}
