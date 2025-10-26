package com.example.comptecourant.exceptions;

public class CompteCourantException extends RuntimeException {
    public CompteCourantException(String message) {
        super(message);
    }

    public CompteCourantException(String message, Throwable cause) {
        super(message, cause);
    }
}
