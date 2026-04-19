package com.vti.product_service.controller;

import com.vti.product_service.dto.request.CategoryRequest;
import com.vti.product_service.dto.response.CategoryResponse;
import com.vti.product_service.service.ICategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final ICategoryService categoryService;
    private final ModelMapper modelMapper;

    @GetMapping
    public List<CategoryResponse> getAllCategory(){
        return categoryService.getAllCategory();
    }

    @GetMapping("/{id}")
    public CategoryResponse getCategoryById(@PathVariable String id){
        return categoryService.getCategoryById(id);
    }

    @PostMapping
    public ResponseEntity<String> createCategpory(@RequestBody @Valid CategoryRequest categoryRequest){
        categoryService.createCategory(categoryRequest);
        return new ResponseEntity<>("Created", HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCategory(@RequestBody @Valid CategoryRequest categoryRequest, @PathVariable String id){
        categoryService.updateCategory(categoryRequest, id);
        return new ResponseEntity<>("Updated", HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable String id){
        categoryService.deleteCategory(id);
        return new ResponseEntity<String>("Deleted", HttpStatus.OK);
    }
}
