package com.order_service.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Data
@NoArgsConstructor
public class OrderResponse {
    private String id;
    private String customerId;
    private String status;
    private Integer totalAmount;
    private String promotionCode;
    private Boolean isDeleted;
    private Instant createdDate;
    private String createdBy;
    private Instant lastModifiedDate;
    private String lastModifiedBy;
}
