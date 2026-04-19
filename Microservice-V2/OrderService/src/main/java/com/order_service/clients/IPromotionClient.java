package com.order_service.clients;

import com.order_service.clients.dto.request.PromotionFilter;
import com.order_service.clients.dto.response.PromotionResponse;

public interface IPromotionClient {
    PromotionResponse getPromotionByCode(PromotionFilter promotionFilter);
    Boolean updatePromotion(PromotionFilter promotionFilter);
}
