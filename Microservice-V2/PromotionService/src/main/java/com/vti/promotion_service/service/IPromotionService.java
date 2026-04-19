package com.vti.promotion_service.service;

import com.vti.promotion_service.dto.request.PromotionFilter;
import com.vti.promotion_service.dto.response.PromotionResponse;

public interface IPromotionService {
    PromotionResponse getPromotionById(String id);

    PromotionResponse getPromotionByCode(String code);

    Boolean updatePromotion(PromotionFilter promotionFilter);
}
