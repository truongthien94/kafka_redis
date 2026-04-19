package com.vti.promotion_service.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionRequest {
    @NotEmpty
    private String name;

    @NotEmpty
    private String code;

    @NotEmpty
    private String discountType;

    @NotNull
    @Positive
    private Integer discountValue;

    @NotNull
    @Positive
    private Integer minOrderValue;

    @NotEmpty
    private Instant startDate;

    @NotEmpty
    private Instant endDate;

    @NotNull
    @Positive
    private Integer usageLimit;
}
