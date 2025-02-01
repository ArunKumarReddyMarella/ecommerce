package com.ecommerce.order.service.impl;

import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.exception.OrderItemAlreadyExistsException;
import com.ecommerce.order.repository.OrderItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceImplTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

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
    void testGetOrderItems() {
        Pageable pageable = mock(Pageable.class);
        Page<OrderItem> expectedPage = createTestOrderItemPage(pageable);
        when(orderItemRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<OrderItem> actualPage = orderItemService.getOrderItems(pageable);

        assertEquals(expectedPage, actualPage);
        for(int i = 0; i < expectedPage.getContent().size(); i++) {
            assertOrderItemFields(expectedPage.getContent().get(i), actualPage.getContent().get(i));
        }
        verify(orderItemRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetOrderItemsByOrderId() {
        String orderId = "ORD-001";
        Pageable pageable = mock(Pageable.class);
        Page<OrderItem> expectedOrderItems = createTestOrderItemPage(pageable);
        when(orderItemRepository.findByOrderId(orderId, pageable)).thenReturn(expectedOrderItems);

        Page<OrderItem> actualOrderItems = orderItemService.getOrderItemsByOrderId(orderId, pageable);

        assertEquals(expectedOrderItems, actualOrderItems);
        for (int i = 0; i < expectedOrderItems.getContent().size(); i++) {
            assertOrderItemFields(expectedOrderItems.getContent().get(i), actualOrderItems.getContent().get(i));
        }
        verify(orderItemRepository, times(1)).findByOrderId(orderId, pageable);
    }


    @Test
    void testGetOrderItemById_ExistingOrderItem() {
        OrderItem orderItem = createTestOrderItem();
        when(orderItemRepository.findById(orderItem.getOrderItemId())).thenReturn(Optional.of(orderItem));

        OrderItem foundOrderItem = orderItemService.getOrderItemById(orderItem.getOrderItemId());

        assertEquals(orderItem, foundOrderItem);
        assertOrderItemFields(orderItem, foundOrderItem);
        verify(orderItemRepository, times(1)).findById(orderItem.getOrderItemId());
    }

    @Test
    void testGetOrderItemById_NonExistingOrderItem() {
        String orderItemId = "NON-EXISTENT-ID";
        when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.empty());

        OrderItem foundOrderItem = orderItemService.getOrderItemById(orderItemId);

        assertNull(foundOrderItem);
        verify(orderItemRepository, times(1)).findById(orderItemId);
    }

    @Test
    void testCreateOrderItem_newOrderItem() {
        OrderItem newOrderItem = createTestOrderItem();
        newOrderItem.setOrderItemId(null);
        when(orderItemRepository.saveAndFlush(any(OrderItem.class))).thenReturn(newOrderItem);

        OrderItem createdOrderItem = orderItemService.createOrderItem(newOrderItem);

        assertNotNull(createdOrderItem.getOrderItemId());
        assertOrderItemFields(newOrderItem, createdOrderItem);
        verify(orderItemRepository, times(1)).saveAndFlush(any(OrderItem.class));
    }

    @Test
    void testCreateOrderItem_existingOrderItem() {
        OrderItem existingOrderItem = createTestOrderItem();

        when(orderItemRepository.existsById(existingOrderItem.getOrderItemId())).thenReturn(false);
        when(orderItemRepository.saveAndFlush(any(OrderItem.class))).thenReturn(existingOrderItem);
        OrderItem createdOrderItem = orderItemService.createOrderItem(existingOrderItem);

        assertNotNull(createdOrderItem.getOrderItemId());
        assertOrderItemFields(existingOrderItem, createdOrderItem);
        verify(orderItemRepository, times(1)).existsById(existingOrderItem.getOrderItemId());
        verify(orderItemRepository, times(1)).saveAndFlush(any(OrderItem.class));
    }

    @Test
    void testCreateOrderItem_AlreadyExists() {
        OrderItem existingOrderItem = createTestOrderItem();
        when(orderItemRepository.existsById(existingOrderItem.getOrderItemId())).thenReturn(true);

        assertThrows(OrderItemAlreadyExistsException.class, () -> orderItemService.createOrderItem(existingOrderItem));

        verify(orderItemRepository, times(1)).existsById(existingOrderItem.getOrderItemId());
        verify(orderItemRepository, never()).saveAndFlush(any());
    }

    @Test
    void testUpdateOrderItem_Successful() {
        OrderItem updatedOrderItem = createTestOrderItem();
        when(orderItemRepository.saveAndFlush(any(OrderItem.class))).thenReturn(updatedOrderItem);

        OrderItem result = orderItemService.updateOrderItem(updatedOrderItem);

        assertEquals(updatedOrderItem, result);
        assertOrderItemFields(updatedOrderItem, result);
        verify(orderItemRepository, times(1)).saveAndFlush(updatedOrderItem);
    }

    @Test
    void testDeleteOrderItem() {
        String orderItemId = "OIT-001";
        doNothing().when(orderItemRepository).deleteById(orderItemId); // No exception thrown

        orderItemService.deleteOrderItem(orderItemId);

        verify(orderItemRepository, times(1)).deleteById(orderItemId);
    }

    @Test
    void testPatchOrderItem_Successful() {
        String orderItemId = "OIT-001";
        Map<String, Object> updates = new HashMap<>();
        updates.put("quantity", 5);

        OrderItem existingOrderItem = createTestOrderItem();
        when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.of(existingOrderItem));
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderItem result = orderItemService.patchOrderItem(orderItemId, updates);

        assertEquals(5, existingOrderItem.getQuantity());
        assertOrderItemFields(existingOrderItem, result);
        verify(orderItemRepository, times(1)).findById(orderItemId);
        verify(orderItemRepository, times(1)).save(existingOrderItem);
    }

    @Test
    void testPatchOrderItem_InvalidUpdateField() {
        String orderItemId = "OIT-001";
        Map<String, Object> updates = new HashMap<>();
        updates.put("invalidField", "value");

        OrderItem existingOrderItem = createTestOrderItem();
        when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.of(existingOrderItem));

        assertThrows(IllegalArgumentException.class, () -> orderItemService.patchOrderItem(orderItemId, updates));

        verify(orderItemRepository, times(1)).findById(orderItemId);
        verify(orderItemRepository, never()).save(any(OrderItem.class));
    }

    private void assertOrderItemFields(OrderItem expectedOrderItem, OrderItem actualOrderItem) {
        assertEquals(expectedOrderItem.getOrderItemId(), actualOrderItem.getOrderItemId());
        assertEquals(expectedOrderItem.getOrderId(), actualOrderItem.getOrderId());
        assertEquals(expectedOrderItem.getProductId(), actualOrderItem.getProductId());
        assertEquals(expectedOrderItem.getQuantity(), actualOrderItem.getQuantity());
        assertEquals(expectedOrderItem.getPrice(), actualOrderItem.getPrice());
    }
}
