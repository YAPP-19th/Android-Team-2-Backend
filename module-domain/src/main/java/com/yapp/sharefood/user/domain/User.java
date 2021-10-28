package com.yapp.sharefood.user.domain;

import com.yapp.sharefood.common.domain.BaseEntity;
import com.yapp.sharefood.userflavor.domain.UserFlavor;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @Column(length = 60, unique = true)
    private String nickname;

    @Enumerated
    private OAuthInfo oAuthInfo;

    @OneToMany(mappedBy = "user")
    private List<UserFlavor> userFlavors = new ArrayList<>();

    @Builder
    public User(Long id, String oauthId, String name, OAuthType oAuthType, String nickname) {
        this.id = id;
        this.oAuthInfo = OAuthInfo.of(oauthId, name, oAuthType);
        this.nickname = nickname;
    }

    public void changeNickname(String newNickname) {
        this.nickname = newNickname;
    }
}
