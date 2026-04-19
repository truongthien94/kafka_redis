package com.order_service.repository;

import com.order_service.entity.Order;
import com.order_service.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IOrderRepository extends JpaRepository<Order, String>, JpaSpecificationExecutor<Order> {
}
