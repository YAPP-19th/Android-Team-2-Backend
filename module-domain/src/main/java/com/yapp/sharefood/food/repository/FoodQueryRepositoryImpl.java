package com.yapp.sharefood.food.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.common.utils.QueryUtils;
import com.yapp.sharefood.flavor.domain.Flavor;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.dto.FoodRecommendSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.yapp.sharefood.category.domain.QCategory.category;
import static com.yapp.sharefood.flavor.domain.QFlavor.flavor;
import static com.yapp.sharefood.food.domain.QFood.food;
import static com.yapp.sharefood.food.domain.QFoodFlavor.foodFlavor;

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

    @Override
    public List<Food> findRecommendFoods(FoodRecommendSearch foodRecommendSearch) {
        if (QueryUtils.isEmpty(foodRecommendSearch.getCategories()) || QueryUtils.isEmpty(foodRecommendSearch.getFlavors())) {
            return new ArrayList<>();
        }

        return queryFactory.selectFrom(food)
                .innerJoin(food.foodFlavors.foodFlavors, foodFlavor)
                .where(
                        containCategories(foodRecommendSearch.getCategories()),
                        containFlavors(foodRecommendSearch.getFlavors())
                )
                .groupBy(food.id)
                .orderBy(food.numberOfLikes.desc())
                .limit(foodRecommendSearch.getTop())
                .fetch();
    }

    private BooleanExpression containCategories(List<Category> categories) {
        return categories == null ? null : food.category.in(categories);
    }

    public BooleanExpression containFlavors(List<Flavor> flavors) {
        return flavors == null ? null : flavor.in(flavors);
    }
}
