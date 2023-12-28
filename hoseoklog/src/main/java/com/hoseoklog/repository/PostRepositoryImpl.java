package com.hoseoklog.repository;

import static com.hoseoklog.domain.QPost.post;

import com.hoseoklog.domain.Post;
import com.hoseoklog.request.PostSearchRequest;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;

public class PostRepositoryImpl implements PostRepositoryCustom {

    public PostRepositoryImpl(final JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> findPosts(final PostSearchRequest postSearchRequest) {
        return jpaQueryFactory.selectFrom(post)
                .limit(postSearchRequest.size())
                .offset(postSearchRequest.offset())
                .orderBy(post.id.desc())
                .fetch();
    }
}
