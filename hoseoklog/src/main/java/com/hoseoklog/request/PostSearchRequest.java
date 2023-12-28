package com.hoseoklog.request;

import java.util.Objects;
import lombok.Builder;

public record PostSearchRequest(
        Integer page,
        Integer size
) {

    private static final int MIN_PAGE = 1;
    private static final int MAX_SIZE = 2000;

    @Builder
    public PostSearchRequest {
        if (Objects.isNull(page)) {
            page = 1;
        }
        if (Objects.isNull(size)) {
            size = 10;
        }
    }

    public long offset() {
        return (long) (Math.max(page, MIN_PAGE) - 1) * Math.min(size, MAX_SIZE);
    }
}
