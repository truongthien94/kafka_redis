package com.vti.product_service.dto.response;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryResponse {
    private String id;
    private String name;
    private String parentId;
}
