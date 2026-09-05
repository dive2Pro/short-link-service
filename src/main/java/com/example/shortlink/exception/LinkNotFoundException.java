package com.example.shortlink.exception;

public class LinkNotFoundException extends RuntimeException {
    public LinkNotFoundException(String message) {
        super("Short link not found: " + message);
    }
}
