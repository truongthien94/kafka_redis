package com.order_service.clients.impl;

import com.order_service.clients.IProductClient;
import com.order_service.clients.dto.request.LockProductRequest;
import com.order_service.clients.dto.request.ProductFilter;
import com.order_service.clients.dto.response.ProductResponse;
import com.order_service.common.BaseResponse;
import com.order_service.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductClientImpl implements IProductClient {
    private final WebClient.Builder webClientBuilder;

    @Value("${client.product.uri}")
    private String productUri;

    @Override
    public List<ProductResponse> search(ProductFilter productFilter) {

        BaseResponse<List<ProductResponse>> productResponse = webClientBuilder.build()
                .post()
                .uri(productUri+"/search")
                .bodyValue(productFilter)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponse<List<ProductResponse>>>() {})
                .block();
        if (productResponse == null){
            throw new ApplicationException("Call product-service error");
        }
        return productResponse.getData();
    }

    @Override
    public Boolean lock(LockProductRequest lockProductRequest) {
        BaseResponse<Boolean> productResponse = webClientBuilder.build()
                .put()
                .uri(productUri)
                .bodyValue(lockProductRequest)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponse<Boolean>>() {})
                .block();
        if (productResponse == null){
            throw new ApplicationException("Call product-service error");
        }
        return productResponse.getData();
    }
    }

