package com.yapp.sharefood.bookmark.domain;

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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"food_id", "user_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark extends BaseEntity {

    @Id
    @Column(name = "bookmark_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "food_id")
    private Food food;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Bookmark(User user) {
        this.user = user;
    }

    public Bookmark of(User user) {
        return new Bookmark(user);
    }

    public void assignFood(Food food) {
        if (Objects.isNull(food)) {
            throw new FoodNotFoundException();
        }

        if (Objects.nonNull(this.food)) {
            throw new InvalidOperationException("한번 할당된 게시글은 변경할 수 없습니다.");
        }

        this.food = food;
    }
}
