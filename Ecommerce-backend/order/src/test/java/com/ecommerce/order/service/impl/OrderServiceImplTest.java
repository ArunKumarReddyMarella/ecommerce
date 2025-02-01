package com.ecommerce.order.service.impl;

import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.exception.OrderAlreadyExistException;
import com.ecommerce.order.exception.OrderNotFoundException;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.service.OrderItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemService orderItemService;

    @InjectMocks
    private OrderServiceImpl orderService;

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
    void testGetOrders() {
        Pageable pageable = mock(Pageable.class);
        Page<Order> expectedPage = createTestOrderPage(pageable);
        when(orderRepository.findAll(pageable)).thenReturn(expectedPage);
        Page<Order> actualPage = orderService.getOrders(pageable);
        assertEquals(expectedPage, actualPage);
        for(int i = 0; i < expectedPage.getContent().size(); i++) {
            assertOrderFields(expectedPage.getContent().get(i), actualPage.getContent().get(i));
        }
        verify(orderRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetOrderById_ExistingOrder() {
        Order order = createTestOrder();
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        Order foundOrder = orderService.getOrderById(order.getOrderId());

        assertEquals(order, foundOrder);
        assertOrderFields(order, foundOrder);
        verify(orderRepository, times(1)).findById(order.getOrderId());
    }

    @Test
    void testGetOrderById_NonExistingOrder() {
        String orderId = "NON-EXISTENT-ID";
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(orderId));

        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void testCreateOrder_Successful() {
        Order newOrder = createTestOrder();
        when(orderRepository.saveAndFlush(any(Order.class))).thenReturn(newOrder);
        Order createdOrder = orderService.createOrder(newOrder);
        assertNotNull(createdOrder.getOrderId());
        assertOrderFields(newOrder, createdOrder);
        verify(orderRepository, times(1)).saveAndFlush(any(Order.class));
    }

    @Test
    void testCreateOrder_AlreadyExists() {
        Order existingOrder = createTestOrder();
        when(orderRepository.existsById(existingOrder.getOrderId())).thenReturn(true);
        assertThrows(OrderAlreadyExistException.class, () -> orderService.createOrder(existingOrder));

        verify(orderRepository, times(1)).existsById(existingOrder.getOrderId());
        verify(orderRepository, never()).saveAndFlush(any());
    }

    @Test
    void testCreateOrder_newOrder() {
        Order newOrder = createTestOrder();
        newOrder.setOrderId(null);
        when(orderRepository.saveAndFlush(any(Order.class))).thenReturn(newOrder);
        Order createdOrder = orderService.createOrder(newOrder);
        assertNotNull(createdOrder.getOrderId());
        assertOrderFields(newOrder, createdOrder);
        verify(orderRepository, times(1)).saveAndFlush(any(Order.class));
    }

    @Test
    void testUpdateOrder_Successful() {
        Order updatedOrder = createTestOrder();
        when(orderRepository.existsById(updatedOrder.getOrderId())).thenReturn(true);
        when(orderRepository.saveAndFlush(any(Order.class))).thenReturn(updatedOrder);

        Order result = orderService.updateOrder(updatedOrder);

        assertEquals(updatedOrder, result);
        assertOrderFields(updatedOrder, result);
        verify(orderRepository, times(1)).existsById(updatedOrder.getOrderId());
        verify(orderRepository, times(1)).saveAndFlush(updatedOrder);
    }

    @Test
    void testUpdateOrder_NotFound() {
        Order nonExistingOrder = createTestOrder();
        when(orderRepository.existsById(nonExistingOrder.getOrderId())).thenReturn(false);
        assertThrows(OrderNotFoundException.class, () -> orderService.updateOrder(nonExistingOrder));
        verify(orderRepository, times(1)).existsById(nonExistingOrder.getOrderId());
        verify(orderRepository, never()).saveAndFlush(any());
    }

    @Test
    void testPatchOrder_Successful() {
        String orderId = "ORD-001";
        Map<String, Object> updates = new HashMap<>();
        updates.put("totalAmount", BigDecimal.valueOf(200.00));

        Order existingOrder = createTestOrder();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.patchOrder(orderId, updates);

        assertEquals(BigDecimal.valueOf(200.00), existingOrder.getTotalAmount());
        assertOrderFields(existingOrder, existingOrder);
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(existingOrder);
    }

    @Test
    void testPatchOrder_NotFound() {
        String nonExistingOrderId = "NON-EXISTENT-ID";
        Map<String, Object> updates = new HashMap<>();
        updates.put("totalAmount", BigDecimal.valueOf(200.00));
        when(orderRepository.findById(nonExistingOrderId)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderService.patchOrder(nonExistingOrderId, updates));
        verify(orderRepository, times(1)).findById(nonExistingOrderId);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testPatchOrder_InvalidField() {
        String orderId = "ORD-001";
        Order existingOrder = createTestOrder();
        Map<String, Object> updates = new HashMap<>();
        updates.put("invalidField", "invalidValue");
        when(orderRepository.findById(existingOrder.getOrderId())).thenReturn(Optional.of(existingOrder));
        assertThrows(IllegalArgumentException.class, () -> orderService.patchOrder(orderId, updates));
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testDeleteOrder_Successful() {
        String orderId = "ORD-001";
        when(orderRepository.existsById(orderId)).thenReturn(true);
        doNothing().when(orderRepository).deleteById(orderId);
        orderService.deleteOrder(orderId);

        verify(orderRepository, times(1)).existsById(orderId);
        verify(orderRepository, times(1)).deleteById(orderId);
    }

    @Test
    void testDeleteOrder_NotFound() {
        String orderId = "NON-EXISTENT-ID";
        when(orderRepository.existsById(orderId)).thenReturn(false);
        assertThrows(OrderNotFoundException.class, () -> orderService.deleteOrder(orderId));
        verify(orderRepository, times(1)).existsById(orderId);
        verify(orderRepository, never()).deleteById(anyString());
    }

    @Test
    void testGetOrderItemsByUserId() {
        String userId = "USER-001";
        Pageable pageable = mock(Pageable.class);
        Page<OrderItem> expectedOrderItems = createTestOrderItemPage(pageable);
        when(orderRepository.findOrderIdByUserId(userId)).thenReturn("ORD-001");
        when(orderItemService.getOrderItemsByOrderId("ORD-001", pageable)).thenReturn(expectedOrderItems);

        Page<OrderItem> actualOrderItems = orderService.getOrderItemsByUserId(userId, pageable);

        assertEquals(expectedOrderItems, actualOrderItems);
        for (int i = 0; i < expectedOrderItems.getContent().size(); i++) {
            assertOrderItemFields(expectedOrderItems.getContent().get(i), actualOrderItems.getContent().get(i));
        }
        verify(orderRepository, times(1)).findOrderIdByUserId(userId);
        verify(orderItemService, times(1)).getOrderItemsByOrderId("ORD-001", pageable);
    }

    private void assertOrderFields(Order expected, Order actual) {
        assertEquals(expected.getOrderId(), actual.getOrderId());
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getTotalAmount(), actual.getTotalAmount());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getOrderDate(), actual.getOrderDate());
    }

    private void assertOrderItemFields(OrderItem expected, OrderItem actual) {
        assertEquals(expected.getOrderItemId(), actual.getOrderItemId());
        assertEquals(expected.getOrderId(), actual.getOrderId());
        assertEquals(expected.getProductId(), actual.getProductId());
        assertEquals(expected.getQuantity(), actual.getQuantity());
        assertEquals(expected.getPrice(), actual.getPrice());
    }
}
