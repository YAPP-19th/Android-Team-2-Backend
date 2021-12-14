package com.yapp.sharefood.food.domain;

import com.yapp.sharefood.common.exception.NotFoundException;
import com.yapp.sharefood.favorite.domain.Favorite;
import com.yapp.sharefood.favorite.exception.AlreadyFavoriteExistException;
import com.yapp.sharefood.favorite.exception.FavoriteNotFoundException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Favorites {
    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> favorites = new ArrayList<>();

    public int getSize() {
        return this.favorites.size();
    }

    public void assignFavorite(Food food, Favorite favorite) {
        if(Objects.isNull(favorite)) {
            throw new FavoriteNotFoundException();
        }

        validateAlreadyFavoriteUser(favorite.getUser().getId());

        favorite.assignFood(food);
    }

    public void deleteFavorite(Long userId) {
        Favorite favorite = findFavoriteByUserId(userId);
        favorites.remove(favorite);
    }

    private Favorite findFavoriteByUserId(Long userId) {
        return this.favorites.stream()
                .filter(favorite -> favorite.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(FavoriteNotFoundException::new);
    }

    private void validateAlreadyFavoriteUser(Long userId) {
        if (isAlreadyFavorite(userId)) {
            throw new AlreadyFavoriteExistException();
        }
    }

    public boolean isAlreadyFavorite(Long userId) {
        return favorites.stream()
                .anyMatch(favorite -> favorite.getUser().getId().equals(userId));
    }
}
