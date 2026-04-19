package com.order_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "price",nullable = false)
    private Integer price;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}
