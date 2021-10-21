package com.yapp.sharefood.category.repository;

import com.yapp.sharefood.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
