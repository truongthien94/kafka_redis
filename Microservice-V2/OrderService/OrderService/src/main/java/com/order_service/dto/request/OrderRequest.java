package com.order_service.dto.request;

import com.order_service.dto.response.OrderItemResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class OrderRequest {
    @NotEmpty
    private String customerId;

    @NotEmpty
    @Valid
    private List<OrderItemRequest> orderItems;

    private String promotionCode;
}
