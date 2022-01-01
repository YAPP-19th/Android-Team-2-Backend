package com.yapp.sharefood.favorite.dto;

import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.dto.FoodImageDto;
import com.yapp.sharefood.user.domain.User;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteFoodDto {
    private Long id;
    private String foodTitle;
    private String categoryName;
    private int price;
    private long numberOfLikes;
    private boolean isMeFavorite;
    private List<FoodImageDto> foodImages;

    private FavoriteFoodDto(Long id, String title,
                            String categoryName,
                            int price,
                            long numberOfLikes,
                            boolean isMeFavorite,
                            List<FoodImageDto> images) {
        this.id = id;
        this.foodTitle = title;
        this.categoryName = categoryName;
        this.price = price;
        this.numberOfLikes = numberOfLikes;
        this.isMeFavorite = isMeFavorite;
        this.foodImages = images;
    }

    public static FavoriteFoodDto of(Long id, String title,
                                     String categoryName,
                                     int price,
                                     long numberOfLikes,
                                     boolean isMeFavorite,
                                     List<FoodImageDto> images) {
        return new FavoriteFoodDto(id, title, categoryName, price, numberOfLikes, isMeFavorite, images);
    }

    public static FavoriteFoodDto foodToFavoriteFoodDto(User user, Food food) {
        List<FoodImageDto> foodImageDtos = food.getImages().getImages().stream()
                .map(FoodImageDto::toFoodImageDto)
                .collect(Collectors.toList());
        return new FavoriteFoodDto(food.getId(),
                food.getFoodTitle(),
                food.getCategory().getName(),
                food.getPrice(),
                food.getNumberOfLikes(),
                food.isMeFavorite(user),
                foodImageDtos);
    }
}
