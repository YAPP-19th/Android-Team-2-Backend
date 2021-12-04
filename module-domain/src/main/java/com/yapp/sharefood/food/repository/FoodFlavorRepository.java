package com.yapp.sharefood.food.repository;

import com.yapp.sharefood.food.domain.FoodFlavor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodFlavorRepository extends JpaRepository<FoodFlavor, Long> {
}
