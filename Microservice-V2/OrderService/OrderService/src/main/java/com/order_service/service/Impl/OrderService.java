package com.order_service.service.Impl;

import com.order_service.clients.IPromotionClient;
import com.order_service.clients.IProductClient;
import com.order_service.clients.dto.request.LockProductItemRequest;
import com.order_service.clients.dto.request.ProductFilter;
import com.order_service.clients.dto.request.PromotionFilter;
import com.order_service.clients.dto.response.ProductResponse;
import com.order_service.clients.dto.response.PromotionResponse;
import com.order_service.common.OrderStatus;
import com.order_service.dto.request.OrderItemRequest;
import com.order_service.dto.request.OrderRequest;
import com.order_service.dto.request.OrderStatusUpdate;
import com.order_service.dto.response.OrderResponse;
import com.order_service.entity.Order;
import com.order_service.entity.OrderItem;
import com.order_service.events.OrderCreatedEvent;
import com.order_service.exception.ApplicationException;
import com.order_service.mapper.OrderMapper;
import com.order_service.repository.IOrderItemRepository;
import com.order_service.repository.IOrderRepository;
import com.order_service.service.IOrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService implements IOrderService {

    private final IOrderRepository orderRepository;
    private final IOrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final IProductClient productClient;
    private final IPromotionClient promotionCient;
    private final KafkaTemplate<Object, OrderCreatedEvent> kafkaTemplate;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        // Lấy thông tin mã giảm giá
        PromotionResponse promotionResponse = null;
        PromotionFilter promotionFilter = PromotionFilter.builder().code(orderRequest.getPromotionCode()).build();

        if (orderRequest.getPromotionCode() != null) {
            promotionResponse = promotionCient.getPromotionByCode(promotionFilter);

            if(promotionResponse.getUsageLimit() == 0){
                throw new ApplicationException("Đã dùng hết số lượng giới hạn mã giảm giá");
            }

            if (promotionResponse == null || promotionResponse.getCode() == null) {
                throw new ApplicationException("Mã giảm giá không hợp lệ");
            }

            // Validate Ngày Bắt đầu && Kết thúc giảm giá

            if (promotionResponse.getStartDate() != null && Instant.now().isBefore(promotionResponse.getStartDate())) {
                throw new ApplicationException("Mã giảm giá chưa bắt đầu");
            }

            if (promotionResponse.getEndDate() != null && Instant.now().isAfter(promotionResponse.getEndDate())) {
                throw new ApplicationException("Mã giảm giá đã hết hạn");
            }
        }

        // validate sản phẩm có tồn tại hay không
        List<String> productIds = new ArrayList<>();
        List<OrderItemRequest> orderItemRequests = orderRequest.getOrderItems();
        for (OrderItemRequest orderItemRequest : orderItemRequests) {
            productIds.add(orderItemRequest.getProductId());
        }

        ProductFilter productFilter = ProductFilter.builder().ids(productIds).build();
        List<ProductResponse> productResponses = productClient.search(productFilter);

        Map<String, ProductResponse> productMap = new HashMap<>();
        for (ProductResponse productResponse : productResponses) {
            productMap.put(productResponse.getId(), productResponse);
        }

        Order order = new Order();
        order.setCustomerId(orderRequest.getCustomerId());
        order.setStatus(OrderStatus.NEW.name());
        order.setPromotionCode(orderRequest.getPromotionCode());
        order.setTotalAmount(0);
        Order saveOrder = orderRepository.save(order);

        int totalAmount = 0;
        List<OrderItem> orderItems = new ArrayList<>();
        List<LockProductItemRequest> lockProductItemRequests = new ArrayList<>();

        for (OrderItemRequest orderItemRequest : orderItemRequests) {
            Integer price = productMap.get(orderItemRequest.getProductId()).getPrice();
            Integer stock = productMap.get(orderItemRequest.getProductId()).getStock();

            if (price == null) {
                throw new ApplicationException("Product Not Found");
            }

            if(stock == 0){
                throw new ApplicationException("Out of Stock");
            }

            if(orderItemRequest.getQuantity() > stock){
                throw new ApplicationException("Không đủ số lượng tồn kho");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(saveOrder.getId());
            orderItem.setProductId(orderItemRequest.getProductId());
            orderItem.setQuantity(orderItemRequest.getQuantity());
            orderItem.setPrice(price);

            orderItems.add(orderItem);
            totalAmount += price * orderItemRequest.getQuantity();
            lockProductItemRequests.add(new LockProductItemRequest(orderItem.getProductId(), orderItem.getQuantity()));
        }

        // validate giá trị tối thiểu đơn hàng
        if (promotionResponse != null) {
            if (totalAmount < promotionResponse.getMinOrderValue()) {
                throw new ApplicationException("Đơn hàng không đủ điều kiện áp mã giảm giá.");
            }
            Integer discount = 0;
            if ("PERCENT".equals(promotionResponse.getDiscountType())) {
                discount = totalAmount * promotionResponse.getDiscountValue() / 100;
            } else if ("AMOUNT".equals(promotionResponse.getDiscountType())) {
                discount = promotionResponse.getDiscountValue();
            }
            totalAmount = totalAmount - discount;
            if (totalAmount < 0) {
                totalAmount = 0;
            }
            promotionCient.updatePromotion(promotionFilter);
        }

//        productClient.lock(new LockProductRequest(lockProductItemRequests))
        saveOrder.setTotalAmount(totalAmount);
        List<OrderItem> createdOrderItem = orderItemRepository.saveAll(orderItems);
        Order createdOrder = orderRepository.save(saveOrder);

        OrderCreatedEvent orderCreatedEvent = orderMapper.toEvent(createdOrder);
        orderCreatedEvent.setOrderItems(createdOrderItem);
        kafkaTemplate.send("order_created", orderCreatedEvent);
        log.info("Publish new order success to order_created : [{}]", orderCreatedEvent);
        return orderMapper.to(createdOrder);
    }

    @Override
    public List<OrderResponse> getAllOrder() {
        return orderMapper.toOrderRes(orderRepository.findAll());
    }

    @Override
    public Order updateStatus(OrderStatusUpdate orderStatusUpdate) {
        Order order = orderRepository.findById(orderStatusUpdate.getId())
                .orElseThrow(() -> new ApplicationException("Errors"));

        order.setStatus(orderStatusUpdate.getStatus());

        return orderRepository.save(order);
    }


}

