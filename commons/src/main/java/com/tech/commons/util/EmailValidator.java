package com.tech.commons.util;

import com.tech.commons.exception.InvalidEmailIdException;
import org.springframework.stereotype.Component;

@Component
public class EmailValidator implements Validator<String> {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    @Override
    public boolean validate(String email) throws InvalidEmailIdException {
        if (email == null || email.isEmpty()) {
            throw new InvalidEmailIdException("Email cannot be null or empty.");
        }
        if (!email.matches(EMAIL_REGEX)) {
            throw new InvalidEmailIdException(getErrorMessage());
        }
        return true;
    }

    @Override
    public String getErrorMessage() {
        return "Email must be a valid format, e.g., user@example.com";
    }
}
