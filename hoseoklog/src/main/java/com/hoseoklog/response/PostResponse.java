package com.hoseoklog.response;


import com.hoseoklog.domain.Post;
import lombok.Builder;

public record PostResponse(
        String title,
        String content
) {

    @Builder
    public PostResponse {
    }

    public static PostResponse from(final Post post) {
        return PostResponse.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }
}
