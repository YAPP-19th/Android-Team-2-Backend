package com.yapp.sharefood.food.domain;

import com.yapp.sharefood.common.exception.InvalidOperationException;
import com.yapp.sharefood.flavor.domain.Flavor;
import lombok.Getter;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Embeddable
public class FoodFlavors {
    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodFlavor> foodFlavors = new ArrayList<>();

    public void addAllFlavors(List<Flavor> flavors, Food food) {
        for (Flavor flavor : flavors) {
            addFlavor(flavor, food);
        }
    }

    public void addAllFlavors(Set<Flavor> flavors, Food food) {
        for (Flavor flavor : flavors) {
            addFlavor(flavor, food);
        }
    }

    private void addFlavor(Flavor flavor, Food food) {
        validateDuplicateFlavor(flavor);
        foodFlavors.add(new FoodFlavor(food, flavor));
    }

    private void validateDuplicateFlavor(Flavor flavor) {
        if (foodFlavors.stream().anyMatch(foodFlavor -> foodFlavor.isSameFlavor(flavor))) {
            throw new InvalidOperationException("이미 등록된 flavor입니다.");
        }
    }

    public void updateFlavors(List<Flavor> flavors, Food food) {
        Set<Flavor> flavorSet = new HashSet<>(flavors);
        this.foodFlavors.removeIf(foodFlavor -> !flavorSet.contains(foodFlavor.getFlavor())); // remove
        Set<Flavor> existFlavors = getExistFlavors();
        addAllFlavors(flavorSet.stream()
                .filter(flavor -> !existFlavors.contains(flavor))
                .collect(Collectors.toSet()), food);
    }

    private Set<Flavor> getExistFlavors() {
        return foodFlavors.stream()
                .map(FoodFlavor::getFlavor)
                .collect(Collectors.toSet());
    }
}
