package com.yapp.sharefood.food.repository.query;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.common.order.SortType;
import com.yapp.sharefood.common.utils.QueryUtils;
import com.yapp.sharefood.flavor.domain.Flavor;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.domain.FoodReportStatus;
import com.yapp.sharefood.food.domain.FoodStatus;
import com.yapp.sharefood.food.dto.FoodPageSearch;
import com.yapp.sharefood.food.dto.FoodRecommendSearch;
import com.yapp.sharefood.food.dto.OrderType;
import com.yapp.sharefood.tag.domain.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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
    private static final Long EMPTY_LIKE_NUMBER = 0L;

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
                        reportStatusNormal(),
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
    public List<Food> findFoodNormalSearch(FoodPageSearch foodPageSearch) {
        List<Long> foodsIds = queryFactory.select(food.id)
                .from(food)
                .where(
                        goeMinPrice(foodPageSearch.getMinPrice()),
                        loeMaxPrice(foodPageSearch.getMaxPrice()),
                        lessThanCreateTime(foodPageSearch.getSearchTime()),
                        eqCategory(foodPageSearch.getCategory()),
                        reportStatusNormal(),
                        statusShared()
                )
                .orderBy(findCriteria(foodPageSearch.getOrder(), foodPageSearch.getSort()))
                .limit(foodPageSearch.getSize())
                .offset(foodPageSearch.getOffset() * foodPageSearch.getSize())
                .fetch();

        return findFoodWithCategoryByIds(foodsIds);
    }

    @Override
    public List<Food> findFoodFilterWithTag(FoodPageSearch foodPageSearch) {
        QueryUtils.validateNotEmptyList(foodPageSearch.getTags());

        return queryFactory.selectFrom(food)
                .where(
                        food.id.in(
                                JPAExpressions
                                        .select(foodTag.food.id)
                                        .from(foodTag)
                                        .where(
                                                foodTag.food.eq(food),
                                                goeMinPrice(foodPageSearch.getMinPrice()),
                                                loeMaxPrice(foodPageSearch.getMaxPrice()),
                                                lessThanCreateTime(foodPageSearch.getSearchTime()),
                                                eqCategory(foodPageSearch.getCategory()),
                                                containTags(foodPageSearch.getTags()),
                                                reportStatusNormal(),
                                                statusShared()
                                        )
                        )
                )
                .orderBy(findCriteria(foodPageSearch.getOrder(), foodPageSearch.getSort()))
                .limit(foodPageSearch.getSize())
                .offset(foodPageSearch.getOffset() * foodPageSearch.getSize())
                .fetch();
    }

    @Override
    public List<Food> findFoodFilterWithFlavor(FoodPageSearch foodPageSearch) {
        QueryUtils.validateNotEmptyList(foodPageSearch.getFlavors());

        return queryFactory.selectFrom(food)
                .where(
                        food.id.in(
                                JPAExpressions
                                        .select(foodFlavor.food.id)
                                        .from(foodFlavor)
                                        .where(
                                                foodFlavor.food.eq(food),
                                                goeMinPrice(foodPageSearch.getMinPrice()),
                                                loeMaxPrice(foodPageSearch.getMaxPrice()),
                                                lessThanCreateTime(foodPageSearch.getSearchTime()),
                                                eqCategory(foodPageSearch.getCategory()),
                                                containFlavors(foodPageSearch.getFlavors()),
                                                reportStatusNormal(),
                                                statusShared()
                                        )
                        )
                )
                .orderBy(findCriteria(foodPageSearch.getOrder(), foodPageSearch.getSort()))
                .limit(foodPageSearch.getSize())
                .offset(foodPageSearch.getOffset() * foodPageSearch.getSize())
                .fetch();
    }

    private BooleanExpression lessThanCreateTime(LocalDateTime searchTime) {
        return food.createDate.loe(searchTime);
    }

    private BooleanExpression loeMaxPrice(Integer maxPrice) {
        return maxPrice == null ? null : food.price.loe(maxPrice);
    }

    private BooleanExpression goeMinPrice(Integer minPrice) {
        return minPrice == null ? null : food.price.goe(minPrice);
    }

    private BooleanExpression reportStatusNormal() {
        return food.reportStatus.eq(FoodReportStatus.NORMAL);
    }

    private BooleanExpression statusShared() {
        return food.foodStatus.eq(FoodStatus.SHARED);
    }

    private BooleanExpression eqCategory(Category category) {
        return category == null ? null : food.category.eq(category);
    }

    private BooleanExpression containCategories(List<Category> categories) {
        if (QueryUtils.isEmpty(categories)) {
            return null;
        }

        return food.category.in(categories);
    }

    public BooleanExpression containTags(List<Tag> tags) {
        if (QueryUtils.isEmpty(tags)) {
            return null;
        }

        return foodTag.tag.in(tags);
    }

    public BooleanExpression containFlavors(List<Flavor> flavors) {
        if (QueryUtils.isEmpty(flavors)) {
            return null;
        }

        return foodFlavor.flavor.in(flavors);
    }

    public BooleanExpression notZeroLike() {
        return food.numberOfLikes.ne(EMPTY_LIKE_NUMBER);
    }

    private OrderSpecifier<?> findCriteria(OrderType orderType, SortType sortType) {
        if (sortType == SortType.ID) {
            return food.id.desc();
        }

        if (sortType == SortType.LIKE) {
            return food.numberOfLikes.desc();
        }

        if (sortType == SortType.PRICE) {
            if (orderType == OrderType.ASC) {
                return food.price.asc();
            }

            return food.price.desc();
        }

        return food.id.desc();
    }
}
