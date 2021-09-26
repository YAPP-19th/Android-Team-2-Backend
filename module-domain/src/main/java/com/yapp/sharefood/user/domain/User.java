package com.yapp.sharefood.user.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"oauthId", "oAuthType"})
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 60, unique = true)
    private String nickname;

    @Enumerated
    private OAuthInfo oAuthInfo;

    @Builder
    public User(String oauthId, String name, OAuthType oAuthType, String nickname) {
        this.oAuthInfo = OAuthInfo.of(oauthId, name, oAuthType);
        this.nickname = nickname;
    }
}
