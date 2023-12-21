package com.hoseoklog.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostCreateRequest {

    @NotBlank(message = "제목을 입력해주세요")
    private String title;

    @NotBlank(message = "본문을 입력해주세요")
    private String content;

    private PostCreateRequest() {
    }

    // 빌더의 장점
    // - 가독성 좋음 (값 생성에 대한 유리함)
    // - 필요한 값만 받을 수 있다. -> 오버로딩 가능한 조건
    //
    @Builder
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
