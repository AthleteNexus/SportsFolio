package com.tech.commons.util;

import com.tech.commons.exception.InvalidPasswordException;
import org.springframework.stereotype.Component;

@Component
public class PasswordValidator implements Validator<String> {

    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$";

    @Override
    public boolean validate(String password) throws InvalidPasswordException {
        if (password == null || password.isEmpty()) {
            throw new InvalidPasswordException("Password cannot be null or empty.");
        }
        if (!password.matches(PASSWORD_REGEX)) {
            throw new InvalidPasswordException(getErrorMessage());
        }
        return true;
    }

    @Override
    public String getErrorMessage() {
        return "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, and one digit.";
    }
}
