package com.vti.product_service.controller;

import com.vti.product_service.common.BaseResponse;
import com.vti.product_service.dto.request.LockProductRequest;
import com.vti.product_service.dto.request.ProductRequest;
import com.vti.product_service.dto.response.ProductResponse;
import com.vti.product_service.entity.Product;
import com.vti.product_service.form.ProductFilter;
import com.vti.product_service.form.ProductFilterForm;
import com.vti.product_service.service.IProductService;
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
@RequestMapping("/api/v1/products")
public class ProductController {
    private final IProductService productService;
    private final ModelMapper modelMapper;

    @GetMapping
    public List<ProductResponse> getAllProduct(ProductFilterForm form){
        return productService.getAllProduct(form);
    }

    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable String id){
        return productService.getProductById(id);
    }

    @PostMapping("/search")
    public ResponseEntity<BaseResponse<List<Product>>> search(@RequestBody ProductFilter productFilter){
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(productService.search(productFilter),"Success"));
    }

    @PostMapping
    public ResponseEntity<String> createProduct(@RequestBody @Valid ProductRequest productRequest){
        productService.createProduct(productRequest);
        return new ResponseEntity<>("Created", HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(@RequestBody @Valid ProductRequest productRequest, @PathVariable String id){
        productService.updateProduct(productRequest, id);
        return new ResponseEntity<>("Updated", HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable String id){
        productService.deleteProduct(id);
        return new ResponseEntity<String>("Deleted", HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<BaseResponse<Boolean>> lock(@RequestBody @Valid LockProductRequest lockProductRequest){
        return ResponseEntity.status(HttpStatus.OK).body( new BaseResponse<>(productService.lock(lockProductRequest),"Success"));
    }


}
