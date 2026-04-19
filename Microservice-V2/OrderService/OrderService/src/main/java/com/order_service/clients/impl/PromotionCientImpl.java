package com.order_service.clients.impl;

import com.order_service.clients.IPromotionClient;
import com.order_service.clients.dto.request.PromotionFilter;
import com.order_service.clients.dto.response.PromotionResponse;
import com.order_service.common.BaseResponse;
import com.order_service.exception.ApplicationException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.function.EntityResponse;

@Data
@RequiredArgsConstructor
@Component
public class PromotionCientImpl implements IPromotionClient {
    private final WebClient.Builder webClientBuilder;
    @Value("${client.promotion.uri}")
    private String promotionUri;

    @Override
    public PromotionResponse getPromotionByCode(PromotionFilter promotionFilter) {
        String code = promotionFilter.getCode();
        BaseResponse<PromotionResponse> promotionResponse = webClientBuilder.build()
                .get()
                .uri(promotionUri + "/code/" + code)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponse<PromotionResponse>>() {
                })
                .block();
        if (promotionResponse == null) {
            throw new ApplicationException("Call promotion-service error");
        }
        return promotionResponse.getData();
    }

    @Override
    public Boolean updatePromotion(PromotionFilter promotionFilter) {
        BaseResponse<Boolean> updatePromotion = webClientBuilder.build()
                .put()
                .uri(promotionUri)
                .bodyValue(promotionFilter)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponse<Boolean>>() {
                })
                .block();
        if (updatePromotion == null) {
            throw new ApplicationException("Call Promotion_Service Error");
        }
        return updatePromotion.getData();
    }


}
