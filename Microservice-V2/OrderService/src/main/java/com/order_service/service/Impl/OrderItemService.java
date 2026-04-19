package com.order_service.service.Impl;

import com.order_service.dto.response.OrderItemResponse;
import com.order_service.mapper.OrderItemMapper;
import com.order_service.repository.IOrderItemRepository;
import com.order_service.service.IOrderItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderItemService implements IOrderItemService {
    private final IOrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    public List<OrderItemResponse> getAllOrderItem() {
        return orderItemMapper.toOrderItem(orderItemRepository.findAll());
    }
}
