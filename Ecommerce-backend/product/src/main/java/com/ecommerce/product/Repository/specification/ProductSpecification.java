package com.ecommerce.product.Repository.specification;

import com.ecommerce.product.Entity.Product;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProductSpecification implements Specification<Product> {

    private Set<String> productIDs;
    private Set<String> columns;
    public ProductSpecification(Set<String> productIDs, Set<String> columns) {
        this.productIDs = productIDs;
        this.columns = columns;
    }

    @Override
    public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        if(!productIDs.isEmpty()) {
            predicates.add(root.get("id").in(productIDs));
        }
        if(!columns.isEmpty()) {
            for (String column : columns) {
                predicates.add(cb.isNotNull(root.get(column)));
            }
        }
        return cb.and(predicates.toArray(new Predicate[0]));
    }

}
