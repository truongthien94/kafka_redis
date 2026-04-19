package com.vti.product_service.service;

import com.vti.product_service.dto.request.CategoryRequest;
import com.vti.product_service.dto.request.ProductRequest;
import com.vti.product_service.dto.response.CategoryResponse;
import com.vti.product_service.dto.response.ProductResponse;

import java.util.List;

public interface ICategoryService {


    List<CategoryResponse> getAllCategory();

    CategoryResponse getCategoryById(String id);

    void createCategory(CategoryRequest categoryRequest);

    void updateCategory(CategoryRequest categoryRequest, String id);

    void deleteCategory(String id);
}
