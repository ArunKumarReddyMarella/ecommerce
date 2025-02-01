package com.ecommerce.order.service.impl;

import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.exception.OrderItemAlreadyExistsException;
import com.ecommerce.order.repository.OrderItemRepository;
import com.ecommerce.order.service.OrderItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
            boolean existingOrderItem = orderItemRepository.existsById(orderItem.getOrderItemId());
            if (existingOrderItem) {
                throw new OrderItemAlreadyExistsException("OrderItem with ID " + orderItem.getOrderItemId() + " already exists.");
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
    public OrderItem patchOrderItem(String orderItemId, Map<String, Object> updates) {
        OrderItem existingOrderItem = orderItemRepository.findById(orderItemId).orElseThrow(() -> new RuntimeException("Order Item not found"));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // for Timestamp support

        try {
            // assuming updates is a Map<String, Object>
            mapper.updateValue(existingOrderItem, updates);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid update field: " + updates);
        }

        return orderItemRepository.save(existingOrderItem);
    }
}
