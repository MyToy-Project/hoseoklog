package com.hoseoklog.response;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.FieldError;

/**
 * { "code": "400", "message": "잘못된 요청입니다.", "validation": { "title": "값을 입력해주세요" } }
 */
@Getter
public final class ErrorResponse {

    private final String code;
    private final String message;
    private final Map<String, String> validation;

    @Builder
    public ErrorResponse(String code, String message, Map<String, String> validation) {
        this.code = code;
        this.message = message;
        this.validation = validation;
    }

    public static ErrorResponse of(final String code, final String message, final List<FieldError> fieldErrors) {
        Map<String, String> validation = fieldErrors.stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage
                ));

        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .validation(validation)
                .build();

    }
}
