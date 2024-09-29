package com.ecommerce.order.controller;

import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.service.OrderItemService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/order-items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @GetMapping
    public ResponseEntity<Page<OrderItem>> getOrderItems(
            @RequestParam(defaultValue = "0") int page,  // Default to page 0
            @RequestParam(defaultValue = "10") int size, // Default to 10 items per page
            @RequestParam(defaultValue = "desc") String sortDirection) {
        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "price"); // Example: sorting by price
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderItem> orderItems = orderItemService.getOrderItems(pageable);
        return ResponseEntity.ok(orderItems);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItem> getOrderItemById(@PathVariable String id) {
        OrderItem orderItem = orderItemService.getOrderItemById(id);
        return ResponseEntity.ok(orderItem);
    }

    @PostMapping
    public ResponseEntity<OrderItem> createOrderItem(@RequestBody @Valid OrderItem orderItem) {
        OrderItem createdOrderItem = orderItemService.createOrderItem(orderItem);
        return ResponseEntity.ok(createdOrderItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItem> updateOrderItem(@PathVariable String id, @RequestBody @Valid OrderItem orderItem) {
        orderItem.setOrderItemId(id); // Ensure ID matches path variable
        OrderItem updatedOrderItem = orderItemService.updateOrderItem(orderItem);
        if (updatedOrderItem == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedOrderItem);
    }

    @PatchMapping("/{orderItemId}")
    public ResponseEntity<OrderItem> patchProduct(@PathVariable String orderItemId, @RequestBody Map<String, Object> updates) {
        OrderItem existingOrderItem = orderItemService.getOrderItemById(orderItemId);
        if (existingOrderItem == null) {
            return ResponseEntity.notFound().build();
        }
        orderItemService.patchOrderItem(orderItemId, updates); // Delegate patching logic to service
        OrderItem orderItem = orderItemService.getOrderItemById(orderItemId); // Refetch after patching
        return ResponseEntity.ok(orderItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrderItem(@PathVariable String id) {
        orderItemService.deleteOrderItem(id);
        return ResponseEntity.ok("Order item deleted successfully!");
    }
}

