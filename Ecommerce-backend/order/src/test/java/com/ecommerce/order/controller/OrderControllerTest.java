package com.ecommerce.order.controller;

import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.service.OrderItemService;
import com.ecommerce.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;
    @Mock
    private OrderItemService orderItemService;
    @InjectMocks
    private OrderController orderController;


    private static Order createTestOrder() {
        Order order = new Order();
        order.setOrderId("ORD-001");
        order.setUserId("USER-001");
        order.setTotalAmount(BigDecimal.valueOf(150.00));
        order.setStatus("PENDING");
        order.setOrderDate(Timestamp.valueOf(LocalDateTime.now()));
        return order;
    }

    private static List<Order> createTestOrderList() {
        List<Order> orders = new ArrayList<>();
        orders.add(createTestOrder());
        orders.add(createTestOrder());
        return orders;
    }

    private static Page<Order> createTestOrderPage(Pageable pageable) {
        return new PageImpl<>(createTestOrderList(), pageable, 2);
    }

    private static List<OrderItem> createOrderItemTestList(){
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(createTestOrderItem());
        orderItems.add(createTestOrderItem());
        return orderItems;
    }
    private static OrderItem createTestOrderItem() {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderItemId("OIT-001");
        orderItem.setOrderId("ORD-001");
        orderItem.setProductId("PRD-001");
        orderItem.setQuantity(2);
        orderItem.setPrice(BigDecimal.valueOf(75.00));
        return orderItem;
    }

    private static Page<OrderItem> createTestOrderItemPage(Pageable pageable) {
        return new PageImpl<>(createOrderItemTestList(), pageable, 2);
    }


    @Test
    void testGetOrders_Success() {
        int page = 0;
        int size = 10;
        String sortDirection = "desc";
        Sort sort = Sort.by(Sort.Direction.DESC, "orderDate");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Order> expectedOrders = createTestOrderPage(pageable);

        when(orderService.getOrders(pageable)).thenReturn(expectedOrders);

        ResponseEntity<Page<Order>> response = orderController.getOrders(page, size, sortDirection);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedOrders, response.getBody());
        for(int i = 0; i < expectedOrders.getContent().size(); i++) {
            assertOrderFields(expectedOrders.getContent().get(i), response.getBody().getContent().get(i));
        }
        verify(orderService, times(1)).getOrders(pageable);
    }

    @Test
    void testGetOrderById_Success() {
        String orderId = "ORD-001";
        Order expectedOrder = createTestOrder();
        when(orderService.getOrderById(orderId)).thenReturn(expectedOrder);

        ResponseEntity<Order> response = orderController.getOrderById(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedOrder, response.getBody());
        assertOrderFields(expectedOrder, response.getBody());
        verify(orderService, times(1)).getOrderById(orderId);
    }

    @Test
    void testCreateOrder_Success() {
        Order newOrder = createTestOrder();
        when(orderService.createOrder(newOrder)).thenReturn(newOrder);

        ResponseEntity<Order> response = orderController.createOrder(newOrder);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newOrder, response.getBody());
        assertOrderFields(newOrder, response.getBody());
        verify(orderService, times(1)).createOrder(newOrder);
    }

    @Test
    void testUpdateOrder_Success() {
        String orderId = "ORD-001";
        Order updatedOrder = createTestOrder();
        when(orderService.updateOrder(updatedOrder)).thenReturn(updatedOrder);

        ResponseEntity<Order> response = orderController.updateOrder(orderId, updatedOrder);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedOrder, response.getBody());
        assertOrderFields(updatedOrder, response.getBody());
        verify(orderService, times(1)).updateOrder(updatedOrder);
    }

    @Test
    void testPatchOrder_Success() {
        String orderId = "ORD-001";
        Map<String, Object> updates = Map.of("totalAmount", BigDecimal.valueOf(200.00));
        Order patchedOrder = createTestOrder();
        patchedOrder.setTotalAmount(BigDecimal.valueOf(200.00));

        when(orderService.patchOrder(orderId, updates)).thenReturn(patchedOrder);

        ResponseEntity<Order> response = orderController.patchOrder(orderId, updates);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(patchedOrder, response.getBody());
        assertOrderFields(patchedOrder, response.getBody());
        verify(orderService, times(1)).patchOrder(orderId, updates);
    }

    @Test
    void testDeleteOrder_Success() {
        String orderId = "ORD-001";

        ResponseEntity<String> response = orderController.deleteOrder(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Order deleted successfully!", response.getBody());
        verify(orderService, times(1)).deleteOrder(orderId);
    }


    @Test
    void testGetOrderItems_Success() {
        String orderId = "ORD-001";
        int page = 0;
        int size = 10;
        String sortDirection = "desc";
        Sort sort = Sort.by(Sort.Direction.DESC, "price");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderItem> expectedOrderItems = createTestOrderItemPage(pageable);

        when(orderItemService.getOrderItemsByOrderId(orderId, pageable)).thenReturn(expectedOrderItems);

        ResponseEntity<Page<OrderItem>> response = orderController.getOrderItems(orderId, page, size, sortDirection);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedOrderItems, response.getBody());
        for(int i = 0; i < expectedOrderItems.getContent().size(); i++) {
            assertOrderItemFields(expectedOrderItems.getContent().get(i), response.getBody().getContent().get(i));
        }
        verify(orderItemService, times(1)).getOrderItemsByOrderId(orderId, pageable);
    }

    private void assertOrderFields(Order expectedOrder, Order actualOrder) {
        assertEquals(expectedOrder.getOrderId(), actualOrder.getOrderId());
        assertEquals(expectedOrder.getUserId(), actualOrder.getUserId());
        assertEquals(expectedOrder.getTotalAmount(), actualOrder.getTotalAmount());
        assertEquals(expectedOrder.getStatus(), actualOrder.getStatus());
        assertEquals(expectedOrder.getOrderDate(), actualOrder.getOrderDate());
    }

    private void assertOrderItemFields(OrderItem expectedOrderItem, OrderItem actualOrderItem) {
        assertEquals(expectedOrderItem.getOrderItemId(), actualOrderItem.getOrderItemId());
        assertEquals(expectedOrderItem.getOrderId(), actualOrderItem.getOrderId());
        assertEquals(expectedOrderItem.getProductId(), actualOrderItem.getProductId());
        assertEquals(expectedOrderItem.getQuantity(), actualOrderItem.getQuantity());
        assertEquals(expectedOrderItem.getPrice(), actualOrderItem.getPrice());
    }
}
