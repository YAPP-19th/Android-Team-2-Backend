package com.yapp.sharefood.food.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yapp.sharefood.food.domain.Food;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.yapp.sharefood.category.domain.QCategory.category;
import static com.yapp.sharefood.food.domain.QFood.food;

@Repository
@RequiredArgsConstructor
public class FoodQueryRepositoryImpl implements FoodQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Food> findFoodWithCategoryByIds(List<Long> ids) {
        if (Objects.isNull(ids) || ids.isEmpty()) {
            return Collections.emptyList();
        }

        return queryFactory.selectFrom(food)
                .leftJoin(food.category, category).fetchJoin()
                .where(inIds(ids))
                .fetch();
    }

    private BooleanExpression inIds(List<Long> ids) {
        return ids == null ? null : food.id.in(ids);
    }
}
