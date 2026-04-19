package com.vti.product_service.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryRequest {
    @NotEmpty
    private String name;
    @NotEmpty
    private String parentId;
}
