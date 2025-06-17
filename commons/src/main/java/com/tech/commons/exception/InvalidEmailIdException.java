package com.tech.commons.exception;

public class InvalidEmailIdException extends RuntimeException {

    public InvalidEmailIdException(String message) {
        super(message);
    }

    public InvalidEmailIdException(String message, Throwable cause) {
        super(message, cause);
    }
}
