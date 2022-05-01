package com.yapp.sharefood.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AyncConfig extends AsyncConfigurerSupport {
    private static final int MAX_THREAD_POOL_SIZE = 100;
    private static final String THREAD_NAME_PREFIX = "Async-Executor-";


    @Bean
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(MAX_THREAD_POOL_SIZE);
        taskExecutor.setThreadNamePrefix(THREAD_NAME_PREFIX);
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncTaskExceptionHandler();
    }

    @Slf4j
    private static class AsyncTaskExceptionHandler extends SimpleAsyncUncaughtExceptionHandler {
        @Override
        public void handleUncaughtException(Throwable ex, Method method, Object... params) {
            log.error("[Aync-Error] 비동기 처리 중 에러가 발생. method {} params: {} exception: {}", method, params, ex);
        }
    }
}
