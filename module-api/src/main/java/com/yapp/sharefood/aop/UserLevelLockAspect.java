package com.yapp.sharefood.aop;

import com.yapp.sharefood.config.lock.UserLevelLock;
import com.yapp.sharefood.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import static com.yapp.sharefood.config.lock.UserlevelLockSql.DEFAULT_USERLEVEL_LOCk_TIME_OUT;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class UserLevelLockAspect {
    private static final String SERVICE_NAME = "like";

    private final UserLevelLock userLevelLock;

    @Pointcut("execution(public * com.yapp.sharefood.like.service.LikeService.saveLike(..))")
    public void createLike() {
    }

    @Pointcut("execution(public * com.yapp.sharefood.like.service.LikeService.deleteLike(..))")
    public void deleteLike() {
    }

    @Around(value = "createLike() && args(user, foodId)", argNames = "joinPoint,user,foodId")
    public Object saveLikeUserLevelLock(ProceedingJoinPoint joinPoint,
                                        User user,
                                        Long foodId) throws Throwable {
        log.info("[user level lock] foodId={}, userId={}", foodId, user.getId());
        return userLevelLock.executeWithLock(
                SERVICE_NAME + "_" + foodId,
                DEFAULT_USERLEVEL_LOCk_TIME_OUT,
                () -> {
                    try {
                        return joinPoint.proceed();
                    } catch (RuntimeException e) {
                        throw e;
                    } catch (Throwable e) {
                        e.printStackTrace();
                        throw new RuntimeException();
                    }
                }
        );
    }


    @Around(value = "deleteLike() && args(user, foodId)", argNames = "joinPoint,user,foodId")
    public Object deleteLikeUserLevelLock(ProceedingJoinPoint joinPoint,
                                          User user,
                                          Long foodId) throws Throwable {
        log.info("[user level lock] foodId={}, userId={}", foodId, user.getId());
        return userLevelLock.executeWithLock(
                SERVICE_NAME + "_" + foodId,
                DEFAULT_USERLEVEL_LOCk_TIME_OUT,
                () -> {
                    try {
                        return joinPoint.proceed();
                    } catch (RuntimeException e) {
                        throw e;
                    } catch (Throwable e) {
                        e.printStackTrace();
                        throw new RuntimeException();
                    }
                }
        );
    }
}
