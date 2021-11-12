package com.yapp.sharefood.like.repository.custom;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.food.domain.FoodStatus;
import com.yapp.sharefood.like.projection.QTopLikeProjection;
import com.yapp.sharefood.like.projection.TopLikeProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.yapp.sharefood.like.domain.QLike.like;

@Repository
@RequiredArgsConstructor
public class LikeCustomRepositoryImpl implements LikeCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<TopLikeProjection> findTopFoodIdsByCount(int top, List<Category> categories, LocalDateTime before, LocalDateTime now) {
        NumberPath<Long> countAlias = Expressions.numberPath(Long.class, "lik_count");

        return queryFactory
                .select(new QTopLikeProjection(
                        like.count().as(countAlias),
                        like.food.id
                ))
                .from(like)
                .where(
                        greatherThanCreateDate(before),
                        lessThanCreateDate(now),
                        eqFoodStatus(FoodStatus.SHARED),
                        inFoodCategories(categories)
                )
                .groupBy(like.food)
                .orderBy(countAlias.desc())
                .limit(top)
                .fetch();
    }

    private BooleanExpression inFoodCategories(List<Category> categories) {
        if (Objects.isNull(categories) || categories.isEmpty()) {
            return null;
        }

        return like.food.category.in(categories);
    }

    private BooleanExpression lessThanCreateDate(LocalDateTime time) {
        return time == null ? null : like.createDate.before(time);
    }

    private BooleanExpression greatherThanCreateDate(LocalDateTime time) {
        return time == null ? null : like.createDate.after(time);
    }

    private BooleanExpression eqFoodStatus(FoodStatus foodStatus) {
        return foodStatus == null ? null : like.food.foodStatus.eq(foodStatus);
    }
}
