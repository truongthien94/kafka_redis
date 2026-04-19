package com.vti.promotion_service.mapper;

import com.vti.promotion_service.dto.response.PromotionResponse;
import com.vti.promotion_service.entity.Promotion;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PromotionMapper {
    PromotionResponse toResponse(Promotion promotion);
    Promotion toPromotion(PromotionResponse promotionResponse);
    List<Promotion> toPromotions(List<PromotionResponse> promotionResponses);
    List<PromotionResponse> toPromotionResponses(List<Promotion> promotions);
}
