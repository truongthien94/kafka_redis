package com.vti.product_service.consumers.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderCreatedEvent extends Order {
    private List<OrderItem> orderItems;
}
