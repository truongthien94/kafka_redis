package com.order_service.clients.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductFilter {

    private List<String> ids;
}
