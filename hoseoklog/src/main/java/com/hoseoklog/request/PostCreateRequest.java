package com.hoseoklog.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

public record PostCreateRequest(
        @NotBlank(message = "제목을 입력해주세요")
        String title,

        @NotBlank(message = "본문을 입력해주세요")
        String content
) {

    @Builder
    public PostCreateRequest {
    }
}
