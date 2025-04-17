package com.example.backend.exceptions;

public class NameValueRequireException extends RuntimeException {
    public NameValueRequireException(String message) {
        super(message);
    }
}
