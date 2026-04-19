package com.vti.product_service.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilter {
    private String id;
    private List<String> ids;
    private String name;
    private Integer price;
    private Integer stock;
    private String categoryId;
}
