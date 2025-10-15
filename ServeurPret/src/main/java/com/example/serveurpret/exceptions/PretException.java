package com.example.serveurpret.exceptions;

/**
 * Exception de base pour toutes les exceptions métier du service Pret
 */
public class PretException extends Exception {
    private String errorCode;
    private int httpStatus;

    public PretException(String message) {
        super(message);
        this.errorCode = "PRET_ERROR";
        this.httpStatus = 500;
    }

    public PretException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = 400;
    }

    public PretException(String message, String errorCode, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public PretException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "PRET_ERROR";
        this.httpStatus = 500;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}