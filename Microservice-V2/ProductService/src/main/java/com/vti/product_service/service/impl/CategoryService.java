package com.vti.product_service.service.impl;

import com.vti.product_service.dto.request.CategoryRequest;
import com.vti.product_service.dto.request.ProductRequest;
import com.vti.product_service.dto.response.CategoryResponse;
import com.vti.product_service.dto.response.ProductResponse;
import com.vti.product_service.entity.Category;
import com.vti.product_service.entity.Product;
import com.vti.product_service.repository.ICategoryRepository;
import com.vti.product_service.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService implements ICategoryService {
    private final ICategoryRepository categoryRepository;
    private final ModelMapper modelMapper;


    @Override
    public List<CategoryResponse> getAllCategory() {
        List<Category> categories = categoryRepository.findAll();
        return modelMapper.map(categories, new TypeToken<List<CategoryResponse>>() {
        }.getType());
    }

    @Override
    public CategoryResponse getCategoryById(String id) {
        Optional<Category> category = categoryRepository.findById(id);
        return modelMapper.map(category, CategoryResponse.class);
    }

    @Override
    public void createCategory(CategoryRequest categoryRequest) {
        Category category = modelMapper.map(categoryRequest, Category.class);
        category.setId(null);
        categoryRepository.save(category);
    }

    @Override
    public void updateCategory(CategoryRequest categoryRequest, String id) {
        Category category = modelMapper.map(categoryRequest, Category.class);
        category.setId(id);
        categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(String id) {
        categoryRepository.deleteById(id);
    }
}
