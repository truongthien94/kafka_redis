package com.order_service.mapper;

import com.order_service.dto.response.OrderResponse;
import com.order_service.entity.Order;
import com.order_service.events.OrderCreatedEvent;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponse to(Order order);
    OrderCreatedEvent toEvent(Order order);
    List<Order> toOrders(List<OrderResponse> orderResponse);
    List<OrderResponse> toOrderRes(List<Order> order);
}
