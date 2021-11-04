package com.yapp.sharefood.image.domain;

import com.yapp.sharefood.common.domain.BaseEntity;
import com.yapp.sharefood.common.exception.InvalidOperationException;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.exception.FoodNotFoundException;
import lombok.Getter;

import javax.persistence.*;
import java.util.Collections;
import java.util.Objects;

@Entity
@Getter
public class Image extends BaseEntity {
    @Id
    @Column(name = "image_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String storeFilename;

    @Column(nullable = false)
    private String realFilename;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    private Food food;

    public void assignFood(Food food) {
        if (Objects.isNull(food)) {
            throw new FoodNotFoundException();
        }
        if (Objects.nonNull(this.food)) {
            throw new InvalidOperationException("사진의 음식을 변경할 수 없습니다");
        }

        this.food = food;
        food.getImages()
                .addImages(Collections.singletonList(this), food);
    }
}
