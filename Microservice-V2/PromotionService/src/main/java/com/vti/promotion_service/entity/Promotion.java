package com.vti.promotion_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Entity
@Table(name = "promotions")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Promotion extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", length = 50, nullable = false)
    private String code;

    @Column(name = "discount_type", nullable = false, length = 20)
    private String discountType;

    @Column(name = "discount_value", nullable = false)
    private Integer discountValue;

    @Column(name = "min_order_value")
    @ColumnDefault("0")
    private Integer minOrderValue;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name="end_date")
    private Instant endDate;

    @Column(name = "usage_limit")
    private Integer usageLimit;

}
