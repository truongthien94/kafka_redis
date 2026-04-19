package com.vti.product_service.specification;

import com.vti.product_service.entity.Product;
import com.vti.product_service.form.ProductFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductSpecificationV2 {
    private static final String SEARCH_ID_IN = "idsIn";

    public static Specification<Product> buildWhere(ProductFilter form) {
        return new SpecificationImpl(SEARCH_ID_IN, form.getIds());
    }

    @AllArgsConstructor
    public static class SpecificationImpl implements Specification<Product> {

        private String key;
        private Object value;

        @Override
        public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            if (value == null) {
                return null;
            }

            switch (key) {
                case SEARCH_ID_IN:
                    if (value instanceof List<?>) {

                        List<?> rawList = (List<?>) value;

                        List<String> ids = new ArrayList<>();

                        for (Object v : rawList) {
                            try {
                                String id = UUID.fromString(v.toString()).toString();
                                ids.add(id);
                            } catch (Exception e) {
                            }
                        }

                        if (ids.isEmpty()) {
                            return null;
                        }

                        return root.get("id").in(ids);
                    }
                    return null;
            }

            return null;
        }
    }
}