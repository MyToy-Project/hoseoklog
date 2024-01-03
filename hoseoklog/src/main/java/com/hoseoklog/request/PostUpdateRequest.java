package com.hoseoklog.request;

import lombok.Builder;

public record PostUpdateRequest(
        String title,
        String content
) {

    @Builder
    public PostUpdateRequest {
    }
}
