package com.yapp.sharefood.food.repository;

import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.food.domain.Food;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Long>, FoodQueryRepository {
    Slice<Food> findByCategory(Category category, Pageable pageable);
}
