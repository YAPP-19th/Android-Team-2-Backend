package com.yapp.sharefood.common.random;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RandomStringCreator {
    public static String createRandomUUIDStr() {
        return UUID.randomUUID().toString();
    }
}
