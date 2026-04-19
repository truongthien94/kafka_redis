package com.vti.product_service.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductRequest {
    @NotEmpty
    private String name;
    @NotNull
    @Positive
    private Integer price;
    @NotNull
    @Positive
    private Integer stock;
    @NotEmpty
    private String categoryId;
}
