package com.vti.promotion_service.controller;

import com.vti.promotion_service.common.BaseResponse;
import com.vti.promotion_service.dto.request.PromotionFilter;
import com.vti.promotion_service.dto.response.PromotionResponse;
import com.vti.promotion_service.service.IPromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/promotions")
public class PromotionController {
    private final IPromotionService promotionService;

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<PromotionResponse>> getPromotionById(@PathVariable String id){
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(promotionService.getPromotionById(id),"Success"));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<BaseResponse<PromotionResponse>> getPromotionByCode(@PathVariable String code){
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(promotionService.getPromotionByCode(code), "Success"));
    }

    @PutMapping
    public ResponseEntity<BaseResponse<Boolean>> updatePromotion(@RequestBody PromotionFilter promotionFilter){
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(promotionService.updatePromotion(promotionFilter), "Success"));
    }
}
