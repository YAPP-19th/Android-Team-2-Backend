package com.yapp.sharefood.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
@Profile({"dev", "local"})
public class RepositoryLoggingAspect {
    @Pointcut("execution(* com.yapp.sharefood..*Repository.*(..))")
    public void loggerPointCut() {
    }

    @Around("loggerPointCut()")
    public Object methodLogger(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result = proceedingJoinPoint.proceed();

        stopWatch.stop();

        String repositoryName = proceedingJoinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = proceedingJoinPoint.getSignature().getName();

        Map<String, Object> params = new HashMap<>();

        try {
            params.put("service", repositoryName);
            params.put("method", methodName);
            params.put("params", Arrays.toString(proceedingJoinPoint.getArgs()));
            params.put("log_time", stopWatch.prettyPrint());
        } catch (Exception e) {
            log.error("LoggingAspect error", e);
        }

        log.info("params : {}", params);
        return result;
    }
}
