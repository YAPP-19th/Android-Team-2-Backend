package com.yapp.sharefood.user.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"oauthId", "name", "oAuthType"})
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickName;

    @Enumerated
    private OAuthInfo oAuthInfo;

    @Builder
    public User(String oauthId, String name, OAuthType oAuthType, String nickName) {
        this.oAuthInfo = OAuthInfo.of(oauthId, name, oAuthType);
        this.nickName = nickName;
    }
}
