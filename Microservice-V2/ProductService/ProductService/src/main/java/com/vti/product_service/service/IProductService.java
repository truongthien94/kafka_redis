package com.vti.product_service.service;

import com.vti.product_service.dto.request.LockProductRequest;
import com.vti.product_service.dto.request.ProductRequest;
import com.vti.product_service.dto.response.ProductResponse;
import com.vti.product_service.entity.Product;
import com.vti.product_service.form.ProductFilter;
import com.vti.product_service.form.ProductFilterForm;
import jakarta.validation.Valid;

import java.util.List;

public interface IProductService {
    List<ProductResponse> getAllProduct(ProductFilterForm form);

    ProductResponse getProductById(String id);

    void createProduct(ProductRequest productRequest);

    void updateProduct(ProductRequest productRequest, String id);

    void deleteProduct(String id);

    List<Product> search(ProductFilter productFilter);

    Boolean lock(LockProductRequest lockProductRequest);
}
