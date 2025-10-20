package com.example.serveurcomptecourant.exceptions;

/**
 * Exception de base pour toutes les exceptions métier du système bancaire
 */
public class BankingException extends Exception {
    
    private final String errorCode;
    private final Object[] parameters;

    public BankingException(String message) {
        super(message);
        this.errorCode = "BANKING_ERROR";
        this.parameters = new Object[0];
    }

    public BankingException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BANKING_ERROR";
        this.parameters = new Object[0];
    }

    public BankingException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.parameters = new Object[0];
    }

    public BankingException(String errorCode, String message, Object... parameters) {
        super(message);
        this.errorCode = errorCode;
        this.parameters = parameters != null ? parameters : new Object[0];
    }

    public BankingException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.parameters = new Object[0];
    }

    public BankingException(String errorCode, String message, Throwable cause, Object... parameters) {
        super(message, cause);
        this.errorCode = errorCode;
        this.parameters = parameters != null ? parameters : new Object[0];
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Object[] getParameters() {
        return parameters;
    }
}