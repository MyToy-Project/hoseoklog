package com.hoseoklog.exception;

public class PostNotFoundException extends BusinessException {

    private static final String MESSAGE = "존재하지 않는 게시글입니다.";

    public PostNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getStatsCode() {
        return 404;
    }
}
