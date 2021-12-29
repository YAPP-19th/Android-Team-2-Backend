package com.yapp.sharefood.favorite.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.common.utils.QueryUtils;
import com.yapp.sharefood.favorite.domain.Favorite;
import com.yapp.sharefood.user.domain.User;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.yapp.sharefood.favorite.domain.QFavorite.favorite;

@RequiredArgsConstructor
public class FavoriteQueryRepositoryImpl implements FavoriteQueryRepository {
    private final JPAQueryFactory queryFactory;

    private List<Favorite> findByIds(List<Long> favoriteIds) {
        if (QueryUtils.isEmpty(favoriteIds)) {
            return new ArrayList<>();
        }

        return queryFactory.selectFrom(favorite)
                .where(
                        favorite.id.in(favoriteIds)
                )
                .fetch();
    }

    @Override
    public List<Favorite> findAllByUserAndCategories(User user, List<Category> categories) {
        if (QueryUtils.isEmpty(categories) || user == null) {
            return new ArrayList<>();
        }

        List<Long> findIds = queryFactory.select(favorite.id)
                .from(favorite)
                .where(
                        containCategories(categories),
                        eqUser(user)
                ).fetch();

        return findByIds(findIds);
    }

    private BooleanExpression containCategories(List<Category> categories) {
        if (QueryUtils.isEmpty(categories)) {
            return null;
        }

        return favorite.food.category.in(categories);
    }

    private BooleanExpression eqUser(User user) {
        return user == null ? null : favorite.user.eq(user);
    }
}
