package com.yapp.sharefood.common.random;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RandomStringCreator {
    public String createRandomUUIDStr() {
        return UUID.randomUUID().toString();
    }
}
