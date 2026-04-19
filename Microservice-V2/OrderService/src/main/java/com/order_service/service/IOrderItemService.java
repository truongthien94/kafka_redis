package com.order_service.service;

import com.order_service.dto.response.OrderItemResponse;

import java.util.List;

public interface IOrderItemService {
    List<OrderItemResponse> getAllOrderItem();
}
