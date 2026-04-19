package com.order_service.clients.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionResponse {
    private String id;
    private String name;
    private String code;
    private String discountType;
    private Integer discountValue;
    private Integer minOrderValue;
    private Instant startDate;
    private Instant endDate;
    private Integer usageLimit;
}
