package com.yapp.sharefood.aop;

import com.yapp.sharefood.annotation.ModifyingQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ModifingQueryAspect {
    private final EntityManager entityManager;

    @Around("@annotation(modifyingQuery)")
    public Object flushAndClearAroundQuery(ProceedingJoinPoint proceedingJoinPoint, ModifyingQuery modifyingQuery) throws Throwable {
        if (modifyingQuery.flushAuto()) {
            entityManager.flush();
            log.debug("before query flush");
        }

        Object response = proceedingJoinPoint.proceed();

        if (modifyingQuery.cleanAuto()) {
            entityManager.clear();
            log.debug("before query clear");
        }

        return response;
    }
}
