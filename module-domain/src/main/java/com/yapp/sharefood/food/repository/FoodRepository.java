package com.yapp.sharefood.food.repository;

import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.food.domain.Food;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Long>, FoodQueryRepository {
    Slice<Food> findByCategory(Category category, Pageable pageable);

    @Query("SELECT f FROM Food f JOIN FETCH f.category WHERE f.id = :id")
    Optional<Food> findByIdWithCategory(@Param("id") Long id);
}
