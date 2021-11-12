package com.yapp.sharefood.food.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yapp.sharefood.food.domain.FoodTag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

import static com.yapp.sharefood.food.domain.QFoodTag.foodTag;
import static com.yapp.sharefood.tag.domain.QTag.tag;

@Repository
@RequiredArgsConstructor
public class FoodTagQueryRepositoryImpl implements FoodTagQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<FoodTag> findFoodtagsWithTag(List<Long> foodTagIds) {
        if (foodTagIds.isEmpty()) {
            return Collections.emptyList();
        }

        return queryFactory.selectFrom(foodTag)
                .innerJoin(foodTag.tag, tag)
                .where(inIds(foodTagIds))
                .fetch();
    }

    private BooleanExpression inIds(List<Long> ids) {
        return ids == null ? null : foodTag.id.in(ids);
    }
}
