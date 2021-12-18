package com.yapp.sharefood.user.domain;

import com.yapp.sharefood.common.domain.BaseEntity;
import com.yapp.sharefood.common.exception.InvalidOperationException;
import com.yapp.sharefood.flavor.domain.Flavor;
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
import java.util.Set;
import java.util.stream.Collectors;

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

    @Column(name = "user_grade")
    @Enumerated(EnumType.STRING)
    private Grade grade;

    @Column(name = "user_grade_point")
    private Integer gradePoint;

    @Column(name = "user_report_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserReportStatus reportStatus;

    @Column(name = "user_report_point", nullable = false)
    private Integer reportPoint;

    @Embedded
    private final OAuthInfo oAuthInfo = new OAuthInfo();

    @Embedded
    private final UserFlavors userFlavors = new UserFlavors();

    @Builder
    public User(Long id, String oauthId, String name, OAuthType oAuthType, String nickname) {
        this.id = id;
        this.nickname = nickname;
        this.grade = Grade.STUDENT;
        this.gradePoint = 0;
        this.oAuthInfo.initOAuthInfo(oauthId, name, oAuthType);

        this.reportStatus = UserReportStatus.NORMAL;
        this.reportPoint = 0;
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

        this.gradePoint += POINT_REGISTER_FOOD;
    }

    public void addPointByOpenFood(Food food) {
        if (food == null) throw new FoodNotFoundException();

        if (!Objects.equals(food.getWriter().getId(), this.id)) {
            throw new InvalidOperationException("내가 작성한 Food가 아닙니다.");
        }

        if (!canEarnPoint(food.getWriter().getGrade())) return;

        this.gradePoint += (food.getFoodStatus().isShared()) ? POINT_OPEN_FOOD : 0;
    }

    public void upgrade() {
        int point = this.gradePoint != null ? this.gradePoint : -1;
        this.grade = Grade.gradeByPoint(point);
    }

    public void updateUserFlavors(List<Flavor> flavors) {
        this.userFlavors.deleteAllFlavors();
        this.userFlavors.addAllFlavors(flavors, this);
    }

    public void addReport(String reportMessage) {
        UserReportType reportType = UserReportType.getFoodReportType(reportMessage);
        this.reportPoint += reportType.getPoint();

        this.reportStatus = UserReportStatus.getReportStatus(this.reportPoint);
        // TODO: 2021/12/12 Token 제거 로직 필요
    }
}
