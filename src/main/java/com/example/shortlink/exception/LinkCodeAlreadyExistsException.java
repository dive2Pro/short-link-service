package com.example.shortlink.exception;

public class LinkCodeAlreadyExistsException extends RuntimeException {
    public LinkCodeAlreadyExistsException(String message) {
        super("Short link code already exists" + message);
    }
}
