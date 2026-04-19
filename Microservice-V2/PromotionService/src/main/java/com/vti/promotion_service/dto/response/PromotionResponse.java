package com.vti.promotion_service.dto.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private boolean isDeleted;
    private Instant createdDate;
    private String createdBy;
    private Instant lastModifiedDate;
    private String lastModifiedBy;
}
