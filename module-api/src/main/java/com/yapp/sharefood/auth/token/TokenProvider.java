package com.yapp.sharefood.auth.token;

import com.yapp.sharefood.user.domain.User;

public interface TokenProvider {
    String createToken(User user);

    Long extractIdByToken(String token);

    boolean isValidToken(String token);
}
