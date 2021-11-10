package com.yapp.sharefood.like.domain;

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

@Entity
@Getter
@Table(name = "likes", uniqueConstraints = {
        @UniqueConstraint(
                name = "unique_key_food_user",
                columnNames = {"user_id", "food_id"}
        )
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like extends BaseEntity {
    @Id
    @Column(name = "like_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "food_id")
    private Food food;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Like(User user) {
        this.user = user;
    }

    public static Like of(User user) {
        return new Like(user);
    }

    public void assignFood(Food food) {
        if (Objects.isNull(food)) {
            throw new FoodNotFoundException();
        }

        if (Objects.nonNull(this.food)) {
            throw new InvalidOperationException("한번 할당된 게시글은 변경할 수 없습니다.");
        }
        food.assignLike(this);
        this.food = food;
    }
}

