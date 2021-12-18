package com.yapp.sharefood.config.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
public class UserLevelLockLocal implements UserLevelLock {
    private final DataSource userLevelLockDataSource;

    @Override
    public <T> T executeWithLock(String userLockName, int timeoutSeconds, Supplier<T> supplier) {
        log.debug("start local mock=[{}], connection=[{}]", userLockName, timeoutSeconds);
        return supplier.get();
    }
}
