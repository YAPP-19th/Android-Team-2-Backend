package com.yapp.sharefood.user.domain;

import com.yapp.sharefood.common.domain.BaseEntity;
import com.yapp.sharefood.common.exception.InvalidOperationException;
import com.yapp.sharefood.flavor.domain.Flavor;
import com.yapp.sharefood.flavor.exception.FlavorNotFoundException;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.exception.FoodNotFoundException;
import com.yapp.sharefood.userflavor.domain.UserFlavor;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.yapp.sharefood.user.domain.Grade.*;

@Getter
@Entity
@Table(name = "user",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"oauthId", "oAuthType"})
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, unique = true)
    private String nickname;

    @Column(name = "user_point", columnDefinition = "integer default 0")
    private Integer point;

    @Column(name = "user_grade")
    @Enumerated(EnumType.STRING)
    private Grade grade;

    @Embedded
    private OAuthInfo oAuthInfo = new OAuthInfo();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserFlavor> userFlavors = new ArrayList<>();

    @Builder
    public User(Long id, String oauthId, String name, OAuthType oAuthType, String nickname) {
        this.id = id;
        this.nickname = nickname;
        this.grade = Grade.STUDENT;
        this.point = 0;
        this.oAuthInfo.initOAuthInfo(oauthId, name, oAuthType);
    }

    public void changeNickname(String newNickname) {
        this.nickname = newNickname;
    }

    public void addPointByRegisterFood(Food food) {
        if (food == null) throw new FoodNotFoundException();

        if (!Objects.equals(food.getWriter().getId(), this.id)) {
            throw new InvalidOperationException("내가 작성한 Food가 아닙니다.");
        }

        if (!canEarnPoint(food.getWriter().getGrade())) return;

        this.point += POINT_REGISTER_FOOD;
    }

    public void addPointByOpenFood(Food food) {
        if (food == null) throw new FoodNotFoundException();

        if (!Objects.equals(food.getWriter().getId(), this.id)) {
            throw new InvalidOperationException("내가 작성한 Food가 아닙니다.");
        }

        if (!canEarnPoint(food.getWriter().getGrade())) return;

        this.point += (food.getFoodStatus().isShared()) ? POINT_OPEN_FOOD : 0;
    }

    public void upgrade() {
        int point = this.point != null ? this.point : -1;
        this.grade = Grade.gradeByPoint(point);
    }

    public void assignFlavors(List<Flavor> flavors) {
        if (Objects.isNull(flavors)) {
            throw new FlavorNotFoundException();
        }
        flavors.forEach(this::validateDuplicateFlavor);

        flavors.stream()
                .map(flavor -> UserFlavor.of(this, flavor))
                .forEach(userFlavors::add);
    }

    private void validateDuplicateFlavor(Flavor flavor) {
        if (userFlavors.stream().anyMatch(userFlavor -> userFlavor.isSame(flavor))) {
            throw new InvalidOperationException("이미 등록된 맛 입니다.");
        }
    }
}
