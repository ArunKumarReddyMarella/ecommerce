package com.ecommerce.order.controller;

import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.service.OrderItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderItemControllerTest {

    @Mock
    private OrderItemService orderItemService;

    @InjectMocks
    private OrderItemController orderItemController;


    private static OrderItem createTestOrderItem() {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderItemId("OIT-001");
        orderItem.setOrderId("ORD-001");
        orderItem.setProductId("PRD-001");
        orderItem.setQuantity(2);
        orderItem.setPrice(BigDecimal.valueOf(75.00));
        return orderItem;
    }

    private static List<OrderItem> createOrderItemTestList(){
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(createTestOrderItem());
        orderItems.add(createTestOrderItem());
        return orderItems;
    }

    private static Page<OrderItem> createTestOrderItemPage(Pageable pageable) {
        return new PageImpl<>(createOrderItemTestList(), pageable, 2);
    }


    @Test
    void testGetOrderItems_Success() {
        int page = 0;
        int size = 10;
        String sortDirection = "desc";
        Sort sort = Sort.by(Sort.Direction.DESC, "price");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderItem> expectedOrderItems = createTestOrderItemPage(pageable);

        when(orderItemService.getOrderItems(pageable)).thenReturn(expectedOrderItems);

        ResponseEntity<Page<OrderItem>> response = orderItemController.getOrderItems(page, size, sortDirection);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedOrderItems, response.getBody());
        for(OrderItem orderItem : expectedOrderItems) {
            assertOrderItemFields(orderItem, response.getBody().getContent().get(0));
        }
        verify(orderItemService, times(1)).getOrderItems(pageable);
    }

    @Test
    void testGetOrderItems_Ascending() {
        int page = 0;
        int size = 10;
        String sortDirection = "asc";
        Sort sort = Sort.by(Sort.Direction.ASC, "price");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderItem> expectedOrderItems = createTestOrderItemPage(pageable);

        when(orderItemService.getOrderItems(pageable)).thenReturn(expectedOrderItems);

        ResponseEntity<Page<OrderItem>> response = orderItemController.getOrderItems(page, size, sortDirection);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedOrderItems, response.getBody());
        for(OrderItem orderItem : expectedOrderItems) {
            assertOrderItemFields(orderItem, response.getBody().getContent().get(0));
        }
        verify(orderItemService, times(1)).getOrderItems(pageable);
    }

    @Test
    void testGetOrderItemById_Success() {
        String orderItemId = "OIT-001";
        OrderItem expectedOrderItem = createTestOrderItem();
        when(orderItemService.getOrderItemById(orderItemId)).thenReturn(expectedOrderItem);

        ResponseEntity<OrderItem> response = orderItemController.getOrderItemById(orderItemId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedOrderItem, response.getBody());
        assertOrderItemFields(expectedOrderItem, response.getBody());
        verify(orderItemService, times(1)).getOrderItemById(orderItemId);
    }

    @Test
    void testCreateOrderItem_Success() {
        OrderItem newOrderItem = createTestOrderItem();
        when(orderItemService.createOrderItem(newOrderItem)).thenReturn(newOrderItem);

        ResponseEntity<OrderItem> response = orderItemController.createOrderItem(newOrderItem);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newOrderItem, response.getBody());
        assertOrderItemFields(newOrderItem, response.getBody());
        verify(orderItemService, times(1)).createOrderItem(newOrderItem);
    }

    @Test
    void testUpdateOrderItem_Success() {
        String orderItemId = "OIT-001";
        OrderItem updatedOrderItem = createTestOrderItem();
        when(orderItemService.updateOrderItem(updatedOrderItem)).thenReturn(updatedOrderItem);

        ResponseEntity<OrderItem> response = orderItemController.updateOrderItem(orderItemId, updatedOrderItem);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedOrderItem, response.getBody());
        assertOrderItemFields(updatedOrderItem, response.getBody());
        verify(orderItemService, times(1)).updateOrderItem(updatedOrderItem);
    }

    @Test
    void testUpdateOrderItem_Failure() {
        String orderItemId = "OIT-001";
        OrderItem updatedOrderItem = createTestOrderItem();
        when(orderItemService.updateOrderItem(updatedOrderItem)).thenReturn(null);

        ResponseEntity<OrderItem> response = orderItemController.updateOrderItem(orderItemId, updatedOrderItem);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(orderItemService, times(1)).updateOrderItem(updatedOrderItem);
    }

    @Test
    void testPatchOrderItem_Success() {
        String orderItemId = "OIT-001";
        Map<String, Object> updates = Map.of("quantity", 5);
        OrderItem existingOrderItem = createTestOrderItem();
        OrderItem patchedOrderItem = createTestOrderItem();
        patchedOrderItem.setQuantity(5);

        when(orderItemService.getOrderItemById(orderItemId)).thenReturn(existingOrderItem);
        when(orderItemService.patchOrderItem(orderItemId, updates)).thenReturn(patchedOrderItem);

        ResponseEntity<OrderItem> response = orderItemController.patchProduct(orderItemId, updates);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(patchedOrderItem, response.getBody());
        assertOrderItemFields(patchedOrderItem, response.getBody());
        verify(orderItemService, times(1)).getOrderItemById(orderItemId);
        verify(orderItemService, times(1)).patchOrderItem(orderItemId, updates);
    }

    @Test
    void testPatchOrderItem_Failure() {
        String orderItemId = "OIT-001";
        Map<String, Object> updates = Map.of("invalidField", "value");
        when(orderItemService.getOrderItemById(orderItemId)).thenReturn(null);

        ResponseEntity<OrderItem> response = orderItemController.patchProduct(orderItemId, updates);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(orderItemService, times(1)).getOrderItemById(orderItemId);
    }

    @Test
    void testDeleteOrderItem_Success() {
        String orderItemId = "OIT-001";

        ResponseEntity<String> response = orderItemController.deleteOrderItem(orderItemId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Order item deleted successfully!", response.getBody());
        verify(orderItemService, times(1)).deleteOrderItem(orderItemId);
    }

    private void assertOrderItemFields(OrderItem expectedOrderItem, OrderItem actualOrderItem) {
        assertEquals(expectedOrderItem.getOrderItemId(), actualOrderItem.getOrderItemId());
        assertEquals(expectedOrderItem.getOrderId(), actualOrderItem.getOrderId());
        assertEquals(expectedOrderItem.getProductId(), actualOrderItem.getProductId());
        assertEquals(expectedOrderItem.getQuantity(), actualOrderItem.getQuantity());
        assertEquals(expectedOrderItem.getPrice(), actualOrderItem.getPrice());
    }
}
