package com.yapp.sharefood.food.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.common.order.SortType;
import com.yapp.sharefood.common.utils.QueryUtils;
import com.yapp.sharefood.flavor.domain.Flavor;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.domain.FoodStatus;
import com.yapp.sharefood.food.dto.FoodOrderType;
import com.yapp.sharefood.food.dto.FoodPageSearch;
import com.yapp.sharefood.food.dto.FoodRecommendSearch;
import com.yapp.sharefood.tag.domain.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.yapp.sharefood.category.domain.QCategory.category;
import static com.yapp.sharefood.food.domain.QFood.food;
import static com.yapp.sharefood.food.domain.QFoodFlavor.foodFlavor;
import static com.yapp.sharefood.food.domain.QFoodTag.foodTag;

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
                        statusShared(),
                        containCategories(foodRecommendSearch.getCategories()),
                        containFlavors(foodRecommendSearch.getFlavors()),
                        notZeroLike()
                )
                .groupBy(food.id)
                .orderBy(food.numberOfLikes.desc())
                .limit(foodRecommendSearch.getTop())
                .fetch();
    }

    @Override
    public List<Food> findPageFoods(FoodPageSearch foodPageSearch) {
        List<Long> foodsIds = queryFromByIsTags(foodPageSearch)
                .orderBy(findCriteria(foodPageSearch.getOrder(), foodPageSearch.getSort()))
                .limit(foodPageSearch.getSize())
                .offset(foodPageSearch.getOffset() * foodPageSearch.getSize())
                .fetch();

        return findFoodWithCategoryByIds(foodsIds);
    }

    private JPAQuery<Long> queryFromByIsTags(FoodPageSearch foodPageSearch) {
        JPAQuery<Long> queryLongFactory = queryFactory.select(food.id)
                .from(food);

        if (!Objects.isNull(foodPageSearch.getTags()) && !foodPageSearch.getTags().isEmpty()) {
            return queryLongFactory
                    .innerJoin(food.foodTags.foodTags, foodTag)
                    .where(
                            eqCategory(foodPageSearch.getCategory()),
                            containTags(foodPageSearch.getTags()),
                            statusShared()
                    )
                    .groupBy(food.id);
        }

        return queryLongFactory.where(
                eqCategory(foodPageSearch.getCategory()),
                statusShared()
        );
    }

    private BooleanExpression statusShared() {
        return food.foodStatus.eq(FoodStatus.SHARED);
    }

    private BooleanExpression eqCategory(Category category) {
        return category == null ? null : food.category.eq(category);
    }

    private BooleanExpression containCategories(List<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            return null;
        }

        return food.category.in(categories);
    }

    public BooleanExpression containTags(List<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }

        return foodTag.tag.in(tags);
    }

    public BooleanExpression containFlavors(List<Flavor> flavors) {
        if (flavors == null || flavors.isEmpty()) {
            return null;
        }

        return foodFlavor.flavor.in(flavors);
    }

    public BooleanExpression notZeroLike() {
        return food.numberOfLikes.ne(0L);
    }

    private OrderSpecifier<?> findCriteria(FoodOrderType foodOrderType, SortType sortType) {
        if (foodOrderType == FoodOrderType.ID) {
            return food.id.desc();
        }

        if (foodOrderType == FoodOrderType.LIKE) {
            return food.numberOfLikes.desc();
        }

        if (foodOrderType == FoodOrderType.PRICE) {
            if (sortType == SortType.ASC) {
                return food.price.asc();
            }

            return food.price.desc();
        }
        // TODO book mark 추가

        return food.id.desc();
    }
}
