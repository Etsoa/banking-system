package com.example.echange.exceptions;

public class EchangeException extends RuntimeException {
    public EchangeException(String message) { super(message); }
    public EchangeException(String message, Throwable cause) { super(message, cause); }
}
