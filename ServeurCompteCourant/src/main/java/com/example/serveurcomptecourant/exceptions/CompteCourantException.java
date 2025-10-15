package com.example.serveurcomptecourant.exceptions;

/**
 * Exception de base pour toutes les exceptions métier du service CompteCourant
 */
public class CompteCourantException extends Exception {
    private String errorCode;
    private int httpStatus;

    public CompteCourantException(String message) {
        super(message);
        this.errorCode = "COMPTE_COURANT_ERROR";
        this.httpStatus = 500;
    }

    public CompteCourantException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = 400;
    }

    public CompteCourantException(String message, String errorCode, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public CompteCourantException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "COMPTE_COURANT_ERROR";
        this.httpStatus = 500;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}