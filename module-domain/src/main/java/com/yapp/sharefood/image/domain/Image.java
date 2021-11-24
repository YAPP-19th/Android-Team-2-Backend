package com.yapp.sharefood.image.domain;

import com.yapp.sharefood.common.domain.BaseEntity;
import com.yapp.sharefood.common.exception.InvalidOperationException;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.exception.FoodNotFoundException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collections;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Builder
    public Image(Long id, String storeFilename, String realFilename) {
        this.id = id;
        this.storeFilename = storeFilename;
        this.realFilename = realFilename;
    }


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
