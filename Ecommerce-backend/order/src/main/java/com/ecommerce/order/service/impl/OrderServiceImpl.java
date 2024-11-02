package com.ecommerce.order.service.impl;

import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.exception.OrderAlreadyExistException;
import com.ecommerce.order.exception.OrderNotFoundException;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.service.OrderItemService;
import com.ecommerce.order.service.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + id));
        return order;
    }

    @Override
    public Order createOrder(Order order) {
        if(order.getOrderId() == null)
            order.setOrderId(UUID.randomUUID().toString());
        else {
            Boolean existingOrder = orderRepository.existsById(order.getOrderId());
            if (existingOrder) {
                throw new OrderAlreadyExistException("Order with ID " + order.getOrderId() + " already exists.");
            }
        }
        return orderRepository.saveAndFlush(order);
    }

    @Override
    public Order updateOrder(Order updatedOrder) {
        Boolean existingOrder = orderRepository.existsById(updatedOrder.getOrderId());
        if (!existingOrder) {
            throw new OrderNotFoundException("Order not found with ID: " + updatedOrder.getOrderId());
        }
        return orderRepository.saveAndFlush(updatedOrder);
    }

    @Override
    public void patchOrder(String orderId, Map<String, Object> updates) {
        Order existingOrder = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
        updates.forEach((key, value) -> {
            try {
                Field field = Order.class.getDeclaredField(key);
                field.setAccessible(true);
                if (field.getType() == Timestamp.class) {
                    try {
                        OffsetDateTime odt = OffsetDateTime.parse((String) value);
                        Timestamp timestampValue = Timestamp.from(odt.toInstant());
                        field.set(existingOrder, timestampValue);
                    } catch (DateTimeParseException e) {
                        throw new IllegalArgumentException("Invalid format for " + key + " TimeStamp field");
                    }
                } else {
                    field.set(existingOrder, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalArgumentException("Invalid update field: " + key);
            }
        });
        orderRepository.save(existingOrder);
    }

    @Override
    public void deleteOrder(String id) {
        Boolean existingOrder = orderRepository.existsById(id);
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
