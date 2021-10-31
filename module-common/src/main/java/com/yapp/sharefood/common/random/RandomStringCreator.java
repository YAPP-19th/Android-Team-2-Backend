package com.yapp.sharefood.common.random;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RandomStringCreator {
    private static final int MAX_NICKNAME_SIZE = 15;

    public String createRandomUUIDStr() {
        return UUID.randomUUID().toString().substring(0, MAX_NICKNAME_SIZE);
    }
}
