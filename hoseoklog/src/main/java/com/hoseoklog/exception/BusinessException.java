package com.hoseoklog.exception;

import java.util.HashMap;
import java.util.Map;

public abstract class BusinessException extends RuntimeException {

    private final Map<String, String> validations = new HashMap<>();

    public BusinessException(final String message) {
        super(message);
    }

    public void addValidation(final String fieldName, final String message) {
        validations.put(fieldName, message);
    }

    public abstract int getStatsCode();

    public Map<String, String> getValidations() {
        return validations;
    }
}
