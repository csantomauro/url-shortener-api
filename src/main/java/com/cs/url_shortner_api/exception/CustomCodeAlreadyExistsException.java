package com.cs.url_shortner_api.exception;

public class CustomCodeAlreadyExistsException extends RuntimeException {
    public CustomCodeAlreadyExistsException(String message) {
        super(message);
    }
}
