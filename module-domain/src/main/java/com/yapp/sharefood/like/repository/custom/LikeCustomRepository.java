package com.yapp.sharefood.like.repository.custom;

import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.like.projection.TopLikeProjection;

import java.time.LocalDateTime;
import java.util.List;

public interface LikeCustomRepository {
    List<TopLikeProjection> findTopFoodIdsByCount(int top, List<Category> categories, LocalDateTime before, LocalDateTime now);
}
