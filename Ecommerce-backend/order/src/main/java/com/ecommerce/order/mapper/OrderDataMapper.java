package com.ecommerce.order.mapper;
import com.ecommerce.order.dto.OrderDataDto;
import com.ecommerce.order.dto.OrderDto;
import com.ecommerce.order.dto.OrderItemDto;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderDataMapper {

    public OrderDataDto toOrderDataDtos(Order order, List<OrderItem> orderItems) {
        OrderDto orderDto = toOrderDto(order);
        List<OrderItemDto> orderItemDtos = toOrderItemDtos(orderItems);
        return new OrderDataDto(orderDto, orderItemDtos);
    }

    public OrderDto toOrderDto(Order order){
        return OrderDto.builder()
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().getValue())
                .build();
    }

    public List<OrderItemDto> toOrderItemDtos(List<OrderItem> orderItems){
        return orderItems.stream().map(this::toOrderItemDto).toList();
    }

    public OrderItemDto toOrderItemDto(OrderItem orderItem){
        return OrderItemDto.builder()
                .productId(orderItem.getProductId())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .build();
    }

}
