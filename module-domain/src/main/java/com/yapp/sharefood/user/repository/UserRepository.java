package com.yapp.sharefood.user.repository;

import com.yapp.sharefood.user.domain.OAuthType;
import com.yapp.sharefood.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.oAuthInfo.oauthId = :oauthId AND u.oAuthInfo.oauthType = :oauthType")
    Optional<User> findByOAuthIdAndOAuthType(@Param("oauthId") String oauthId, @Param("oauthType") OAuthType oauthType);

    boolean existsByNickname(String nickname);

    Optional<User> findById(Long userId);
}
