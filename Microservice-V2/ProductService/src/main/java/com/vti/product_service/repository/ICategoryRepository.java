package com.vti.product_service.repository;

import com.vti.product_service.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ICategoryRepository extends JpaRepository<Category,String>, JpaSpecificationExecutor<Category> {
}
