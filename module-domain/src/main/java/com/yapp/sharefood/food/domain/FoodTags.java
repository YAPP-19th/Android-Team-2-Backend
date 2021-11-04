package com.yapp.sharefood.food.domain;

import lombok.Getter;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@Embeddable
public class FoodTags {
    @OneToMany(mappedBy = "food")
    private List<FoodTag> foodTags = new ArrayList<>();
}
