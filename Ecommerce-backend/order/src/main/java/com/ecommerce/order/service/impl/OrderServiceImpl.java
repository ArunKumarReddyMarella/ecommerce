package com.ecommerce.order.service.impl;

import com.ecommerce.order.dto.OrderDataDto;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.exception.OrderAlreadyExistException;
import com.ecommerce.order.exception.OrderNotFoundException;
import com.ecommerce.order.mapper.OrderDataMapper;
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

import java.util.*;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private OrderDataMapper orderDataMapper;

    @Override
    public Page<Order> getOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    public Order getOrderById(String id) {
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + id));
    }

    /*Eager Loading in OrderService: You might encounter the N+1 problem in the getOrderDataByUserId
    @Query("SELECT o FROM Order o JOIN FETCH o.orderItems WHERE o.userId = :userId")
    List<Order> findOrdersWithOrderItemsByUserId(@Param("userId") String userId);*/


    @Override
    public List<OrderDataDto> getOrderDataByUserId(String userId) {
        List<OrderDataDto> orderDataDtos = new ArrayList<>();
        List<String> orderIds = orderRepository.findOrderIdsByUserId(userId);

//        for(String orderId : orderIds) {
//            Optional<Order> optionalOrder = orderRepository.findById(orderId);
//            if(optionalOrder.isPresent()) {
//                List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(orderId, Pageable.unpaged()).getContent();
//                orderDataDtos.add(orderDataMapper.toOrderDataDtos(optionalOrder.get(), orderItems));
//            }
//        }
//        return orderDataDtos;

        List<Order> orders = orderRepository.findAllById(orderIds);
        orders.forEach(order -> orderDataDtos.add(orderDataMapper.toOrderDataDtos(order, orderItemService.getOrderItemsByOrderId(order.getOrderId(), Pageable.unpaged()).getContent())));
        return orderDataDtos;
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

}
