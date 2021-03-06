package com.yapp.sharefood.food.domain;

import com.yapp.sharefood.bookmark.domain.Bookmark;
import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.category.exception.CategoryNotFoundException;
import com.yapp.sharefood.common.domain.BaseEntity;
import com.yapp.sharefood.common.exception.InvalidOperationException;
import com.yapp.sharefood.favorite.domain.Favorite;
import com.yapp.sharefood.flavor.domain.Flavor;
import com.yapp.sharefood.like.domain.Like;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Food extends BaseEntity {
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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FoodReportStatus reportStatus;

    @Column(length = 50)
    private String writerNickname;

    @Column(nullable = false)
    private long numberOfLikes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private Integer reportPoint;

    @Embedded
    private final FoodTags foodTags = new FoodTags();

    @Embedded
    private final Images images = new Images();

    @Embedded
    private final Likes likes = new Likes();

    @Embedded
    private final Bookmarks bookmarks = new Bookmarks();

    @Embedded
    private final Favorites favorites = new Favorites();

    @Embedded
    private final FoodFlavors foodFlavors = new FoodFlavors();

    @Builder
    public Food(Long id, String foodTitle, int price, String reviewMsg, FoodStatus foodStatus, User writer, Category category) {
        this.id = id;
        this.foodTitle = foodTitle;
        this.price = price;
        this.reviewMsg = reviewMsg;
        this.foodStatus = foodStatus;
        this.numberOfLikes = 0L;
        assignWriter(writer);
        assignCategory(category);

        this.reportStatus = FoodReportStatus.NORMAL;
        this.reportPoint = 0;
    }

    public void assignWriter(User user) {
        if (Objects.isNull(user)) {
            throw new UserNotFoundException();
        }

        if (Objects.nonNull(this.writer)) {
            throw new InvalidOperationException("???????????? ?????? ??? ????????????.");
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

    public long getLikeNumber() {
        return this.numberOfLikes;
    }

    public void assignLike(Like like) {
        if (foodStatus == FoodStatus.MINE) {
            throw new InvalidOperationException("?????? ?????? food??? like??? ??? ??? ????????????.");
        }
        this.likes.assignLike(like, this);
    }

    public void deleteLike(User user) {
        likes.deleteLike(user.getId());
    }

    public void assignBookmark(Bookmark bookmark) {
        if (foodStatus == FoodStatus.MINE) {
            throw new InvalidOperationException("?????? ?????? food??? bookmark??? ??? ??? ????????????.");
        }
        this.bookmarks.assignBookmark(this, bookmark);
    }

    public void deleteBookmark(User user) {
        bookmarks.deleteBookmark(user.getId());
    }

    public void assignFavorite(Favorite favorite) {
        this.favorites.assignFavorite(this, favorite);
    }

    public void deleteFavorite(User user) {
        this.favorites.deleteFavorite(user.getId());
    }

    public void assignWrapperTags(List<TagWrapper> wrapperTags) {
        getFoodTags().addAllTags(wrapperTags, this);
    }

    public void assignFlavors(List<Flavor> flavors) {
        foodFlavors.addAllFlavors(flavors, this);
    }

    public boolean isAuth(User user) {
        return this.writer.getId().equals(user.getId());
    }

    public boolean isMyFood(User user) {
        return Objects.equals(this.writer.getId(), user.getId());
    }

    public boolean isMeLike(User user) {
        return this.likes.isAlreadyLike(user.getId());
    }

    public boolean isMeBookMark(User user) {
        return this.bookmarks.isAlreadyBookmark(user.getId());
    }

    public boolean isMeFavorite(User user) {
        return this.favorites.isAlreadyFavorite(user.getId());
    }

    public void updateAllElements(String foodTitle, String reviewMsg, Integer price, FoodStatus foodStatus, Category category) {
        this.foodTitle = foodTitle;
        this.reviewMsg = reviewMsg;
        this.price = price;
        this.foodStatus = foodStatus;
        this.category = category;
    }

    public void addReport(String reportMessage) {
        FoodReportType reportType = FoodReportType.getFoodReportType(reportMessage);
        reportPoint += reportType.getPoint();

        this.reportStatus = FoodReportStatus.getReportStatus(reportPoint);
    }
}
