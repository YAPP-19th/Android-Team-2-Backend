package com.yapp.sharefood.favorite.domain;

import com.yapp.sharefood.common.domain.BaseEntity;
import com.yapp.sharefood.common.exception.InvalidOperationException;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.exception.FoodNotFoundException;
import com.yapp.sharefood.user.domain.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"food_id", "user_id"}))
public class Favorite extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    private Food food;

    private Favorite(User user) {
        this.user = user;
    }

    public static Favorite of(User user) {
        return new Favorite(user);
    }

    public void assignFood(Food food) {
        if (Objects.isNull(food)) {
            throw new FoodNotFoundException();
        }

        if (Objects.nonNull(this.food)) {
            throw new InvalidOperationException("한번 할당된 게시글은 변경할 수 없습니다.");
        }

        this.food = food;
        this.food.getFavorites().getFavorites().add(this);
    }

    public boolean isSameUser(Long userId) {
        return user.getId().equals(userId);
    }
}
