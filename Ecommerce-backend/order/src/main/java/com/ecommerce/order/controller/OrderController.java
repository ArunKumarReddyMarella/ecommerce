package com.ecommerce.order.controller;

import com.ecommerce.order.dto.OrderDataDto;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.service.OrderItemService;
import com.ecommerce.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    public OrderController(OrderService orderService, OrderItemService orderItemService) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
    }

    @GetMapping
    public ResponseEntity<Page<Order>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "orderDate");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Order> orders = orderService.getOrders(pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable String id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody @Valid Order order) {
        Order createdOrder = orderService.createOrder(order);
        return ResponseEntity.ok(createdOrder);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<Order> updateOrder(@PathVariable String orderId, @RequestBody @Valid Order order) {
        order.setOrderId(orderId);
        Order updatedOrder = orderService.updateOrder(order);
        return ResponseEntity.ok(updatedOrder);
    }

    @PatchMapping("/{orderId}")
    public ResponseEntity<Order> patchOrder(@PathVariable String orderId, @RequestBody Map<String, Object> updates) {
        Order updatedOrder = orderService.patchOrder(orderId, updates);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable String id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok("Order deleted successfully!");
    }

    // get all order items in an order
    @GetMapping("/{orderId}/orderItems")
    public ResponseEntity<Page<OrderItem>> getOrderItems(
            @PathVariable String orderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "price");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(orderId, pageable);
        return ResponseEntity.ok(orderItems);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDataDto>> getUserOrders(@PathVariable String userId){
        return ResponseEntity.ok(orderService.getOrderDataByUserId(userId));
    }

}
