package com.order_service.clients.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LockProductRequest {

    @NotEmpty
    private List<LockProductItemRequest> lockProductItemRequests;

    private String orderId;
}
