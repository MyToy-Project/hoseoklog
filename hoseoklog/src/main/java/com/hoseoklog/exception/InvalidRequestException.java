package com.hoseoklog.exception;

public class InvalidRequestException extends BusinessException {

    private static final String MESSAGE = "잘못된 요청입니다.";

    public InvalidRequestException() {
        super(MESSAGE);
    }

    public InvalidRequestException(final String fieldName, final String message) {
        super(MESSAGE);
        addValidation(fieldName, message);
    }

    @Override
    public int getStatsCode() {
        return 400;
    }
}
