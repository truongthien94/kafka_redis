package com.vti.product_service.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductResponse {
    private String id;
    private String name;
    private Integer price;
    private Integer stock;
    private String categoryId;
}
