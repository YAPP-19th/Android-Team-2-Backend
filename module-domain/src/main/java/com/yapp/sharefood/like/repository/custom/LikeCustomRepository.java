package com.yapp.sharefood.like.repository.custom;

import com.yapp.sharefood.like.projection.TopLikeProjection;

import java.time.LocalDateTime;
import java.util.List;

public interface LikeCustomRepository {
    List<TopLikeProjection> findTopFoodIdsByCount(int top, LocalDateTime before, LocalDateTime now);
}
