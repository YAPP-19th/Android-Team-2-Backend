package com.yapp.sharefood.food.repository;

import com.yapp.sharefood.food.domain.Food;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Long> {
}
