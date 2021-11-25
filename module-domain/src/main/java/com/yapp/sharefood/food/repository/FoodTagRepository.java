package com.yapp.sharefood.food.repository;

import com.yapp.sharefood.food.domain.FoodTag;
import com.yapp.sharefood.food.repository.query.FoodTagQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodTagRepository extends JpaRepository<FoodTag, Long>, FoodTagQueryRepository {
}
