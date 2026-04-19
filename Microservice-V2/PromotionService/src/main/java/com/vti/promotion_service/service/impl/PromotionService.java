package com.vti.promotion_service.service.impl;

import com.vti.promotion_service.dto.request.PromotionFilter;
import com.vti.promotion_service.dto.response.PromotionResponse;
import com.vti.promotion_service.entity.Promotion;
import com.vti.promotion_service.mapper.PromotionMapper;
import com.vti.promotion_service.repository.IPromotionRepository;
import com.vti.promotion_service.service.IPromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionService implements IPromotionService {
    private final IPromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;


    @Override
    public PromotionResponse getPromotionById(String id) {
        return promotionMapper.toResponse(promotionRepository.findById(id).get());
    }

    @Override
    public PromotionResponse getPromotionByCode(String code) {
        Promotion promotion = promotionRepository
                .findByCode(code)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));
        return promotionMapper.toResponse(promotion);
    }

    @Override
    public Boolean updatePromotion(PromotionFilter promotionFilter) {
        if (promotionFilter.getCode() != null) {

            Promotion promotion = promotionRepository
                    .findByCode(promotionFilter.getCode())
                    .orElseThrow(() -> new RuntimeException("Promotion not found"));
            Integer newUsageLimit = promotion.getUsageLimit() - 1;
            // check usage
            promotion.setUsageLimit(newUsageLimit);
            promotionRepository.save(promotion);
        }
        return true;
    }
}
