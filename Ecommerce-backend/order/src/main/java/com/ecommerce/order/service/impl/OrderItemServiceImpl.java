package com.ecommerce.order.service.impl;

import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.repository.OrderItemRepository;
import com.ecommerce.order.service.OrderItemService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Override
    public Page<OrderItem> getOrderItems(Pageable pageable) {
        return orderItemRepository.findAll(pageable);
    }

    @Override
    public Page<OrderItem> getOrderItemsByOrderId(String orderId, Pageable pageable) {
        return orderItemRepository.findByOrderId(orderId, pageable);
    }

    @Override
    public OrderItem getOrderItemById(String id) {
        Optional<OrderItem> optionalOrderItem = orderItemRepository.findById(id);
        return optionalOrderItem.orElse(null);
    }

    @Override
    public OrderItem createOrderItem(OrderItem orderItem) {
        if(orderItem.getOrderItemId() == null)
            orderItem.setOrderItemId(UUID.randomUUID().toString());
        else {
            Optional<OrderItem> existingOrderItem = orderItemRepository.findById(orderItem.getOrderItemId());
            if (existingOrderItem.isPresent()) {
                throw new RuntimeException("OrderItem with ID " + orderItem.getOrderItemId() + " already exists.");
            }
        }
        return orderItemRepository.saveAndFlush(orderItem);
    }

    @Override
    public OrderItem updateOrderItem(OrderItem orderItem) {
        return orderItemRepository.saveAndFlush(orderItem);
    }

    @Override
    public void deleteOrderItem(String id) {
        orderItemRepository.deleteById(id);
    }

    @Override
    public void patchOrderItem(String orderItemId, Map<String, Object> updates) {
        OrderItem existingOrderItem = orderItemRepository.findById(orderItemId).orElseThrow(() -> new RuntimeException("Order Item not found"));

        updates.forEach((key, value) -> {
            try {
                // Use reflection or property accessors to update specific fields based on the key
                Field field = OrderItem.class.getDeclaredField(key);
                field.setAccessible(true);
                field.set(existingOrderItem, value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // Handle potential exceptions (e.g., invalid field name)
                throw new IllegalArgumentException("Invalid update field: " + key);
            }
        });

        orderItemRepository.save(existingOrderItem);
    }
}
