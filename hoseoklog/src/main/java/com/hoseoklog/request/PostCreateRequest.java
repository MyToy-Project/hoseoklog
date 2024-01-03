package com.hoseoklog.request;

import com.hoseoklog.exception.InvalidRequestException;
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

    public void validate() {
        if (title.contains("바보")) {
            throw new InvalidRequestException("title", "제목에 바보를 포함할 수 없습니다.");
        }
    }
}
