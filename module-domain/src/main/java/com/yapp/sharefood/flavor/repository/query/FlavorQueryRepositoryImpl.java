package com.yapp.sharefood.flavor.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yapp.sharefood.flavor.domain.Flavor;
import com.yapp.sharefood.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.yapp.sharefood.flavor.domain.QFlavor.flavor;
import static com.yapp.sharefood.userflavor.domain.QUserFlavor.userFlavor;

@Repository
@RequiredArgsConstructor
public class FlavorQueryRepositoryImpl implements FlavorQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Flavor> findByUser(User user) {
        return queryFactory.select(flavor)
                .from(userFlavor)
                .innerJoin(userFlavor.flavor, flavor)
                .where(userFlavor.user.eq(user))
                .fetch();
    }
}
