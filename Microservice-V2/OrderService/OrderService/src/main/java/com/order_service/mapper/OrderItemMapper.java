package com.order_service.mapper;

import com.order_service.dto.response.OrderItemResponse;
import com.order_service.entity.OrderItem;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    List<OrderItemResponse> toOrderItem(List<OrderItem> orderItems);
}
