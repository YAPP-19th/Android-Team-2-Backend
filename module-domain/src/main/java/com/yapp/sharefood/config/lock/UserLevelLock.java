package com.yapp.sharefood.config.lock;

import java.util.function.Supplier;

public interface UserLevelLock {
    <T> T executeWithLock(String userLockName, int timeoutSeconds, Supplier<T> supplier);
}
