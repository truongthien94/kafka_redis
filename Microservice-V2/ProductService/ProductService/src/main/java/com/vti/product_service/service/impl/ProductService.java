package com.vti.product_service.service.impl;

import com.vti.product_service.dto.request.LockProductItemRequest;
import com.vti.product_service.dto.request.LockProductRequest;
import com.vti.product_service.dto.request.ProductRequest;
import com.vti.product_service.dto.response.ProductResponse;
import com.vti.product_service.entity.Product;
import com.vti.product_service.event.ProductLockedEvent;
import com.vti.product_service.exception.ApplicationException;
import com.vti.product_service.form.ProductFilter;
import com.vti.product_service.form.ProductFilterForm;
import com.vti.product_service.mapper.ProductLockedMapper;
import com.vti.product_service.repository.IProductRepository;
import com.vti.product_service.service.IProductService;
import com.vti.product_service.specification.ProductSpecification;
import com.vti.product_service.specification.ProductSpecificationV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductService implements IProductService {
    private final IProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final ProductLockedMapper productLockedMapper;
    private final KafkaTemplate<Object, ProductLockedEvent> kafkaTemplate;
    private final RedissonClient redissonClient;

    @Override
    public List<ProductResponse> getAllProduct(ProductFilterForm form) {
        Specification<Product> where = ProductSpecification.buildWhere(form);
        List<Product> products = productRepository.findAll(where);
        return modelMapper.map(products, new TypeToken<List<ProductResponse>>(){}.getType());
    }

    @Override
    public ProductResponse getProductById(String id) {
        Optional<Product> product = productRepository.findById(id);
        return modelMapper.map(product, ProductResponse.class);
    }

    @Override
    public void createProduct(ProductRequest productRequest) {
        Product product = modelMapper.map(productRequest,Product.class);
        product.setId(null);
        productRepository.save(product);
    }

    @Override
    public void updateProduct(ProductRequest productRequest, String id) {
        Product product = modelMapper.map(productRequest, Product.class);
        product.setId(id);
        productRepository.save(product);
    }

    @Override
    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    @Override
    public List<Product> search(ProductFilter productFilter) {
        Specification<Product> specification = ProductSpecificationV2.buildWhere(productFilter);
        return productRepository.findAll(specification);
//        return productRepository.findByIdIn(productFilter.getIds());
    }

    @Override
    public Boolean lock(LockProductRequest lockProductRequest) {
        List<String> productIds = lockProductRequest
                .getLockProductItemRequests()
                .stream()
                .map(LockProductItemRequest::getProductId)
                .collect(Collectors.toList());

        Map<String, Integer> productIdQuantityMap = new HashMap<>();
        for(LockProductItemRequest lockProductItemRequest: lockProductRequest.getLockProductItemRequests()){
            productIdQuantityMap.put(lockProductItemRequest.getProductId(), lockProductItemRequest.getQuantity());
        }

//        log.info("Start lock Product: {}", productIds);
        // Lock DB
//        List<Product> products = productRepository.findByIdForUpdate(productIds);
//        List<Product> products = productRepository.findByIdIn(productIds);

//        try {
//            Thread.sleep(4000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        for(Product product: products){
//            Integer newStock = product.getStock() - productIdQuantityMap.get(product.getId());
//            if(newStock < 0){
//                throw new ApplicationException("Quantity out of stock");
//            }
//            product.setStock(newStock);
//        }

        // Lock Redis
        Collections.sort(productIds);
        String lockKey = "lock:products:"+String.join(",", productIds);

        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (lock.tryLock(10, 5, TimeUnit.SECONDS)) {
                log.info("Start lock products : {}", productIds);
                List<Product> products = productRepository.findByIdIn(productIds);
                for (Product product : products) {
                    Integer newStock = product.getStock() - productIdQuantityMap.get(product.getId());
                    if (newStock < 0) {
                        throw new ApplicationException("Quantity out of stock");
                    }
                    product.setStock(newStock);
                }

                productRepository.saveAll(products);
            }

        } catch (Exception ex) {
            log.error("Lock product error {}", ex.getMessage());
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        //kafka

        ProductLockedEvent productLockedEvent = productLockedMapper.toEvent(lockProductRequest);
        productLockedEvent.setOrderId(lockProductRequest.getOrderId());
        kafkaTemplate.send("product_locked", productLockedEvent);
        log.info("Locked product success to product_locked : [{}]", productLockedEvent);

        return true;
    }

}
