package com.vti.promotion_service.repository;

import com.vti.promotion_service.dto.response.PromotionResponse;
import com.vti.promotion_service.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface IPromotionRepository extends JpaRepository<Promotion, String>, JpaSpecificationExecutor<Promotion> {
    Optional<Promotion> findByCode(String code);
}
