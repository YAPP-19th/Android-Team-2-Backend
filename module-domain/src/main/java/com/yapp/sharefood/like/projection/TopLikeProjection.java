package com.yapp.sharefood.like.projection;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class TopLikeProjection {
    private final Long count;
    private final Long foodId;

    @QueryProjection
    public TopLikeProjection(Long count, Long foodId) {
        this.count = count;
        this.foodId = foodId;
    }
}
