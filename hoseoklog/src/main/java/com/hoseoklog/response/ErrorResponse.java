package com.hoseoklog.response;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;
import org.springframework.validation.FieldError;

public record ErrorResponse(String code, String message, Map<String, String> validation) {

    @Builder
    public ErrorResponse {
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
