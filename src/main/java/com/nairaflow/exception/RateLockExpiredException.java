package com.nairaflow.exception;

public class RateLockExpiredException extends RuntimeException {
    public RateLockExpiredException(String message) {
        super(message);
    }
}