package com.yapp.sharefood.user.domain;

import com.yapp.sharefood.common.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    @Embedded
    private OAuthInfo oAuthInfo = new OAuthInfo();

    @Builder
    public User(Long id, String oauthId, String name, OAuthType oAuthType, String nickname) {
        this.id = id;
        this.nickname = nickname;
        this.oAuthInfo.initOAuthInfo(oauthId, name, oAuthType);
    }

    public void changeNickname(String newNickname) {
        this.nickname = newNickname;
    }
}
