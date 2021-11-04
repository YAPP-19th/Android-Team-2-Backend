package com.yapp.sharefood.food.repository;

import com.yapp.sharefood.food.domain.FoodTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodTagRepository extends JpaRepository<FoodTag, Long> {
}
