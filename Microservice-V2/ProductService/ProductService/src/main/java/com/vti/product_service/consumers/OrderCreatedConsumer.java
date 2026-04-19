package com.vti.product_service.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vti.product_service.consumers.dto.OrderCreatedEvent;
import com.vti.product_service.dto.request.LockProductItemRequest;
import com.vti.product_service.dto.request.LockProductRequest;
import com.vti.product_service.service.IProductService;
import com.vti.product_service.service.impl.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedConsumer {
    private final IProductService productService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order_created")
//    @RetryableTopic(
//            attempts = "4",
//            backoff = @Backoff(delay = 2000, multiplier = 2)
//    )
    public void handleOrderCratedEvent(String orderString) throws JsonProcessingException {
        OrderCreatedEvent orderCreatedEvent = objectMapper.readValue(orderString, OrderCreatedEvent.class);
        log.info("Receive order message {}", orderCreatedEvent);

//        if (orderCreatedEvent != null) {
//            throw new RuntimeException("Customize exception");
//        }

        List<LockProductItemRequest> lockProductItems = new ArrayList<>();

        orderCreatedEvent.getOrderItems().forEach(orderItem -> {
            LockProductItemRequest lockProductItem = new LockProductItemRequest();
            lockProductItem.setProductId(orderItem.getProductId());
            lockProductItem.setQuantity(orderItem.getQuantity());

            lockProductItems.add(lockProductItem);
        });

        LockProductRequest lockProductReq = new LockProductRequest();
        lockProductReq.setLockProductItemRequests(lockProductItems);
        lockProductReq.setOrderId(orderCreatedEvent.getId());

        productService.lock(lockProductReq);
        log.info("Success to lock product item of {}", orderCreatedEvent.getId());
    }
}
