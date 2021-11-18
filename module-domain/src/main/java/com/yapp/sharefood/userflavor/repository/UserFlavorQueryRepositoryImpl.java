package com.yapp.sharefood.userflavor.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.userflavor.domain.UserFlavor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.yapp.sharefood.userflavor.domain.QUserFlavor.userFlavor;

@RequiredArgsConstructor
@Repository
public class UserFlavorQueryRepositoryImpl implements UserFlavorQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<UserFlavor> findUserFlavorByUserId(User user) {
        return queryFactory.selectFrom(userFlavor)
                .where(userFlavor.user.eq(user))
                .orderBy(userFlavor.flavor.id.asc())
                .fetch();
    }
}
