package com.tech.commons.util;

import com.tech.commons.exception.InvalidUsernameException;
import com.tech.commons.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class UsernameValidator implements Validator<String> {

    private static final String USERNAME_REGEX = "^[a-zA-Z0-9._-]{3,}$";

    @Override
    public boolean validate(String username) throws InvalidUsernameException {
        if (username == null || username.isEmpty()) {
            throw new InvalidUsernameException("Username cannot be null or empty.");
        }
        if (!username.matches(USERNAME_REGEX)) {
            throw new InvalidUsernameException(getErrorMessage());
        }
        return true;
    }

    @Override
    public String getErrorMessage() {
        return "Username must be at least 3 characters long and can only contain letters, numbers, dots, underscores, and hyphens.";
    }
}
