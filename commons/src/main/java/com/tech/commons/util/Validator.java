package com.tech.commons.util;

import com.tech.commons.exception.ValidationException;

public interface Validator<T> {
    boolean validate(T t) throws ValidationException;
    String getErrorMessage();
}
