package com.order_service.events;

import com.order_service.entity.Order;
import com.order_service.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class OrderCreatedEvent extends Order {
    private List<OrderItem> orderItems;

}
