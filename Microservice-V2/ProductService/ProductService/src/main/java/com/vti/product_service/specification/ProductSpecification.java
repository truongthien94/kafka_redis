package com.vti.product_service.specification;

import com.vti.product_service.entity.Product;
import com.vti.product_service.form.ProductFilterForm;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {
    private static final String SEARCH_NAME = "name";

    public static Specification<Product> buildWhere(ProductFilterForm form) {
        SpecificationImpl whereSearchUsernam = new SpecificationImpl(SEARCH_NAME, form.getSearch());
        return whereSearchUsernam;
    }

        @AllArgsConstructor
        public static class SpecificationImpl implements Specification<Product> {

            private String key;
            private Object value;

            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (value == null) {
                    return null;
                }
                switch (key) {
                    case SEARCH_NAME:
                        return criteriaBuilder.like(root.get("name"), "%" + value.toString() + "%");
                }
                return null;
            }}}
