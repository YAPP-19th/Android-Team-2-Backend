package com.yapp.sharefood.tag.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yapp.sharefood.tag.domain.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.yapp.sharefood.tag.domain.QTag.tag;

@Repository
@RequiredArgsConstructor
public class TagQueryRepositoryImpl implements TagQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Tag> findSimilarTags(String similarName, int size) {
        return queryFactory.selectFrom(tag)
                .where(likeNameAtEnd(similarName))
                .limit(size)
                .orderBy(tag.name.asc())
                .fetch();
    }

    private BooleanExpression likeNameAtEnd(String similarName) {
        return similarName != null ? null : tag.name.like(similarName + "%");
    }
}
