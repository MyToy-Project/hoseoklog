package com.hoseoklog.controller;

import lombok.Getter;

@Getter
public class PostCreateRequest {

    private String title;
    private String content;

    private PostCreateRequest() {
    }

    public PostCreateRequest(final String title, final String content) {
        this.title = title;
        this.content = content;
    }

    @Override
    public String toString() {
        return "PostCreateRequest{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
