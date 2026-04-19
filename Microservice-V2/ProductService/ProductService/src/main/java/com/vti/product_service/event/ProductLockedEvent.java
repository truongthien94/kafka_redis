package com.vti.product_service.event;

import com.vti.product_service.dto.request.LockProductRequest;
import com.vti.product_service.entity.Product;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProductLockedEvent extends LockProductRequest {
        private String orderId;
}
