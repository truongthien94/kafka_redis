package com.order_service.clients;

import com.order_service.clients.dto.request.LockProductRequest;
import com.order_service.clients.dto.request.ProductFilter;
import com.order_service.clients.dto.response.ProductResponse;

import java.util.List;

public interface IProductClient {
    List<ProductResponse> search(ProductFilter productFilter);
    Boolean lock(LockProductRequest lockProductRequest);
}
