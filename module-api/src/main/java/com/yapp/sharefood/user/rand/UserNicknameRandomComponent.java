package com.yapp.sharefood.user.rand;

import com.yapp.sharefood.common.random.RandomStringCreator;
import org.springframework.stereotype.Component;

@Component
public class UserNicknameRandomComponent {
    private static final int MAX_NICKNAME_SIZE = 15;

    public String createRandomUserNickname() {
        return RandomStringCreator.createRandomUUIDStr().substring(0, MAX_NICKNAME_SIZE);
    }
}
