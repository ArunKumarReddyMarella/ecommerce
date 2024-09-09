package com.ecommerce.order_items.service;

import com.ecommerce.order_items.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface OrderItemService {
    Page<OrderItem> getOrderItems(Pageable pageable);
    OrderItem getOrderItemById(String id);
    OrderItem createOrderItem(OrderItem orderItem);
    OrderItem updateOrderItem(OrderItem orderItem);
    void deleteOrderItem(String id);

    void patchOrderItem(String orderItemId, Map<String, Object> updates);
}
