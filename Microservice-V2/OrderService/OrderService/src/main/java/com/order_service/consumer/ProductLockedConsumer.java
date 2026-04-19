package com.order_service.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.order_service.common.OrderStatus;
import com.order_service.consumer.dto.ProductLockedEvent;
import com.order_service.dto.request.OrderStatusUpdate;
import com.order_service.service.IOrderService;
import com.order_service.service.Impl.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProductLockedConsumer {
    private final ObjectMapper objectMapper;
    private final IOrderService orderService;

    @KafkaListener(topics = "product_locked")
//    @RetryableTopic(
//            attempts = "4",
//            backoff = @Backoff(delay = 2000, multiplier = 2)
//    )
    public void handleProductLockedEvent(String productString) throws JsonProcessingException {
        ProductLockedEvent productLockedEvent = objectMapper.readValue(productString, ProductLockedEvent.class);
        log.info("Receive product {}", productLockedEvent);

        OrderStatusUpdate orderStatusUpdate = new OrderStatusUpdate();
        orderStatusUpdate.setId(productLockedEvent.getOrderId());
        orderStatusUpdate.setStatus(OrderStatus.PREPARED.toString());

        orderService.updateStatus(orderStatusUpdate);

        log.info("success to lock product item of {}", productLockedEvent.getOrderId());
    }
}
