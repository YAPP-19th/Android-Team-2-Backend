package com.yapp.sharefood.common.random;

import java.util.UUID;

public class RandomStringCreator {
    public static String createRandomUUIDStr() {
        return UUID.randomUUID().toString();
    }
}
