package com.order_service.controller;

import com.order_service.common.BaseResponse;
import com.order_service.dto.response.OrderItemResponse;
import com.order_service.service.IOrderItemService;
import com.order_service.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/v1/order_items")
public class OrderItemController {
    private final IOrderItemService orderItemService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<OrderItemResponse>>> getAllOrderItem() {
        List<OrderItemResponse> data = orderItemService.getAllOrderItem();
        return ResponseEntity.ok(new BaseResponse<>(data, "Success"));
    }
}
