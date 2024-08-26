package com.example.photocontestproject.helpers.specifications;

import com.example.photocontestproject.models.Rating;
import com.example.photocontestproject.models.options.RatingFilterOptions;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class RatingSpecification {
    public static Specification<Rating> filterByOptions(RatingFilterOptions filterOptions) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            filterOptions.getMinScore().ifPresent(minScore ->
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("score"), minScore))
            );

            filterOptions.getMaxScore().ifPresent(maxScore ->
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("score"), maxScore))
            );

            filterOptions.getComment().ifPresent(comment ->
                    predicates.add(criteriaBuilder.like(root.get("comment"), "%" + comment + "%"))
            );

            filterOptions.getCategoryMismatch().ifPresent(categoryMismatch ->
                    predicates.add(criteriaBuilder.equal(root.get("categoryMismatch"), categoryMismatch))
            );

            query.where(predicates.toArray(new Predicate[0]));

            if (filterOptions.getSortBy().isPresent() && filterOptions.getSortOrder().isPresent()) {
                String sortBy = filterOptions.getSortBy().get();
                String sortOrder = filterOptions.getSortOrder().get();
                if (sortOrder.equalsIgnoreCase("asc")) {
                    query.orderBy(criteriaBuilder.asc(root.get(sortBy)));
                } else {
                    query.orderBy(criteriaBuilder.desc(root.get(sortBy)));
                }
            }

            return query.getRestriction();
        };
    }
}
