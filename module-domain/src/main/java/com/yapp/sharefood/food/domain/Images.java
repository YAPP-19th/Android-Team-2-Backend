package com.yapp.sharefood.food.domain;

import com.yapp.sharefood.common.exception.InvalidOperationException;
import com.yapp.sharefood.image.domain.Image;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Images {
    @OneToMany(mappedBy = "food")
    private List<Image> images = new ArrayList<>();

    public void addImages(List<Image> images, Food food) {
        if (Objects.isNull(images)) {
            throw new InvalidOperationException("image가 설정 되어 있지 않습니다.");
        }
        for (Image image : images) {
            if (!this.images.contains(image)) {
                this.images.add(image);
                image.assignFood(food);
            }
        }
    }
}
