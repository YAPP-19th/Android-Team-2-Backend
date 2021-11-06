package com.yapp.sharefood.food.domain;

import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.category.exception.CategoryNotFoundException;
import com.yapp.sharefood.common.exception.InvalidOperationException;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Food {
    @Id
    @Column(name = "food_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String foodTitle;

    @Column(nullable = false)
    private int price;

    private String reviewMsg;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FoodStatus foodStatus;

    @Column(length = 50)
    private String writerNickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Embedded
    private FoodTags foodTags = new FoodTags();

    @Embedded
    private Images images = new Images();

    @Builder
    public Food(Long id, String foodTitle, int price, String reviewMsg, FoodStatus foodStatus, User writer, Category category) {
        this.id = id;
        this.foodTitle = foodTitle;
        this.price = price;
        this.reviewMsg = reviewMsg;
        this.foodStatus = foodStatus;
        assignWriter(writer);
        assignCategory(category);
    }

    public void assignWriter(User user) {
        if (Objects.isNull(user)) {
            throw new UserNotFoundException();
        }

        if (Objects.nonNull(this.writer)) {
            throw new InvalidOperationException("작성자를 바꿀 수 없습니다.");
        }

        this.writer = user;
        this.writerNickname = user.getNickname();
    }

    public void assignCategory(Category category) {
        if (Objects.isNull(category)) {
            throw new CategoryNotFoundException();
        }

        this.category = category;
    }
}
