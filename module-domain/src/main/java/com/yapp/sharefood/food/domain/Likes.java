package com.yapp.sharefood.food.domain;

import com.yapp.sharefood.common.exception.ForbiddenException;
import com.yapp.sharefood.common.exception.InvalidOperationException;
import com.yapp.sharefood.like.domain.Like;
import com.yapp.sharefood.like.exception.LikeNotFoundException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Likes {

    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Like> likes = new ArrayList<>();

    public int getSize() {
        return likes.size();
    }

    public void assignLike(Like like, Food food) {
        if (Objects.isNull(like)) {
            throw new LikeNotFoundException();
        }
        validateAlreadyLikeUser(like.getUser().getId());

        like.assignFood(food);
        if (!likes.contains(like)) {
            likes.add(like);
        }
    }

    public void deleteLike(Long userId) {
        Like like = findLikeByUserId(userId);
        likes.remove(like);
    }

    private Like findLikeByUserId(Long userId) {
        return likes.stream()
                .filter(like -> like.getUser().getId().equals(userId))
                .findAny()
                .orElseThrow(ForbiddenException::new);
    }

    private void validateAlreadyLikeUser(Long userId) {
        if (isAlreadyLike(userId)) {
            throw new InvalidOperationException("이미 like한 사용자 입니다.");
        }
    }

    private boolean isAlreadyLike(Long userId) {
        return likes.stream()
                .anyMatch(like -> like.getUser().getId().equals(userId));
    }
}
