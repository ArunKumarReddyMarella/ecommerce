package com.ecommerce.order.service.impl;

import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.exception.OrderAlreadyExistException;
import com.ecommerce.order.exception.OrderNotFoundException;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.service.OrderItemService;
import com.ecommerce.order.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemService orderItemService;

    @Override
    public Page<Order> getOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    public Order getOrderById(String id) {
//        Optional<Order> optionalOrder = orderRepository.findById(id);
//        return optionalOrder.orElse(null);
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + id));
    }

    @Override
    public Order createOrder(Order order) {
        if(order.getOrderId() == null)
            order.setOrderId(UUID.randomUUID().toString());
        else {
            boolean existingOrder = orderRepository.existsById(order.getOrderId());
            if (existingOrder) {
                throw new OrderAlreadyExistException("Order with ID " + order.getOrderId() + " already exists.");
            }
        }
        return orderRepository.saveAndFlush(order);
    }

    @Override
    public Order updateOrder(Order updatedOrder) {
        boolean existingOrder = orderRepository.existsById(updatedOrder.getOrderId());
        if (!existingOrder) {
            throw new OrderNotFoundException("Order not found with ID: " + updatedOrder.getOrderId());
        }
        return orderRepository.saveAndFlush(updatedOrder);
    }

    @Override
    public Order patchOrder(String orderId, Map<String, Object> updates) {
        Order existingOrder = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // for Timestamp support

        try {
            // assuming updates is a Map<String, Object>
            mapper.updateValue(existingOrder, updates);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid update field: " + updates);
        }
        return orderRepository.save(existingOrder);
    }

    @Override
    public void deleteOrder(String id) {
        boolean existingOrder = orderRepository.existsById(id);
        if (!existingOrder) {
            throw new OrderNotFoundException("Order not found with ID: " + id);
        }
        orderRepository.deleteById(id);
    }

    @Override
    public Page<OrderItem> getOrderItemsByUserId(String userId, Pageable pageable) {
        String orderId = orderRepository.findOrderIdByUserId(userId);
        return orderItemService.getOrderItemsByOrderId(orderId, pageable);
    }
}
