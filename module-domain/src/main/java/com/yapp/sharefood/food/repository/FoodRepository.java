package com.yapp.sharefood.food.repository;

import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.repository.query.FoodQueryRepository;
import com.yapp.sharefood.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Long>, FoodQueryRepository {
    @Query("SELECT f FROM Food f JOIN FETCH f.category WHERE f.id = :id")
    Optional<Food> findByIdWithCategory(@Param("id") Long id);

    @Query("SELECT f FROM Food f WHERE f.id = :foodId AND f.writer = :writer")
    Optional<Food> findByIdWithUser(@Param("foodId") Long foodId, @Param("writer") User user);

    @Query("SELECT f FROM Food f JOIN FETCH f.category JOIN FETCH f.writer WHERE f.id = :foodId")
    Optional<Food> findFoodWithWriterAndCategoryById(@Param("foodId") Long foodId);
}
