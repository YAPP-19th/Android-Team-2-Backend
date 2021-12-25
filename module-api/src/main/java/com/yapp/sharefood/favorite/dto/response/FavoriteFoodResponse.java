package com.yapp.sharefood.favorite.dto.response;

import com.yapp.sharefood.favorite.dto.FavoriteFoodDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteFoodResponse {
    private List<FavoriteFoodDto> favoriteFoods;

    private FavoriteFoodResponse(List<FavoriteFoodDto> favoriteFoods) {
        this.favoriteFoods = favoriteFoods;
    }

    public static FavoriteFoodResponse of(List<FavoriteFoodDto> favoriteFoods) {
        return new FavoriteFoodResponse(favoriteFoods);
    }
}
