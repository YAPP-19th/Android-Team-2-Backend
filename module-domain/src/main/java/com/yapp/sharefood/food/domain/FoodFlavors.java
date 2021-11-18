package com.yapp.sharefood.food.domain;

import com.yapp.sharefood.common.exception.InvalidOperationException;
import com.yapp.sharefood.flavor.domain.Flavor;
import lombok.Getter;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@Embeddable
public class FoodFlavors {
    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodFlavor> foodFlavors = new ArrayList<>();

    public void addAllFlavors(List<Flavor> flavors, Food food) {
        flavors.forEach(this::validateDuplicateFlavor);
        flavors.stream()
                .map(flavor -> new FoodFlavor(food, flavor))
                .forEach(foodFlavors::add);
    }

    private void validateDuplicateFlavor(Flavor flavor) {
        if (foodFlavors.stream().anyMatch(foodFlavor -> foodFlavor.isSameFlavor(flavor))) {
            throw new InvalidOperationException("이미 등록된 flavor입니다.");
        }
    }
}
