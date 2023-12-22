package com.hoseoklog.response;

import com.hoseoklog.domain.Post;
import java.util.List;

public record PostsResponse(List<PostResponse> posts) {

    public static PostsResponse from(final List<Post> posts) {
        return new PostsResponse(
                posts.stream()
                        .map(PostResponse::from)
                        .toList()
        );
    }
}
