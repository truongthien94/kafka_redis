package com.vti.product_service.mapper;

import com.vti.product_service.dto.request.LockProductRequest;
import com.vti.product_service.event.ProductLockedEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductLockedMapper {
    ProductLockedEvent toEvent(LockProductRequest lockProductRequest);
}
