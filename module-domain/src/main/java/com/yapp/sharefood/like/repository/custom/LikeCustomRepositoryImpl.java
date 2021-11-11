package com.yapp.sharefood.like.repository.custom;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yapp.sharefood.food.domain.FoodStatus;
import com.yapp.sharefood.like.projection.QTopLikeProjection;
import com.yapp.sharefood.like.projection.TopLikeProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.yapp.sharefood.like.domain.QLike.like;

@Repository
@RequiredArgsConstructor
public class LikeCustomRepositoryImpl implements LikeCustomRepository {
    private static final NumberPath<Long> COUNT_ALIAS = Expressions.numberPath(Long.class, "counts");

    private final JPAQueryFactory queryFactory;

    @Override
    public List<TopLikeProjection> findTopFoodIdsByCount(int top, LocalDateTime before, LocalDateTime now) {
        return queryFactory.select(new QTopLikeProjection(
                        like.food.id,
                        like.count().as(COUNT_ALIAS)
                ))
                .from(like)
                .where(
                        greatherThanCreateDate(before),
                        lessThanCreateDate(now),
                        eqFoodStatus(FoodStatus.SHARED)
                )
                .groupBy(like.food)
                .orderBy(COUNT_ALIAS.desc())
                .limit(top)
                .fetch();
    }

    private BooleanExpression lessThanCreateDate(LocalDateTime time) {
        return time == null ? null : like.createDate.before(time);
    }

    private BooleanExpression greatherThanCreateDate(LocalDateTime time) {
        return time == null ? null : like.createDate.after(time);
    }

    private BooleanExpression eqFoodStatus(FoodStatus foodStatus) {
        return foodStatus != null ? null : like.food.foodStatus.eq(foodStatus);
    }
}
