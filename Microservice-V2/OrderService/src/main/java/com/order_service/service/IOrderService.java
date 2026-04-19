package com.order_service.service;

import com.order_service.dto.request.OrderRequest;
import com.order_service.dto.request.OrderStatusUpdate;
import com.order_service.dto.response.OrderResponse;
import com.order_service.entity.Order;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IOrderService {
    OrderResponse createOrder(OrderRequest orderRequest);

    List<OrderResponse> getAllOrder();

    Order updateStatus(OrderStatusUpdate orderStatusUpdate);
}
