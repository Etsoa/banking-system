package com.example.serveurcomptecourant.models;

import jakarta.json.bind.annotation.JsonbProperty;
import java.time.LocalDateTime;

/**
 * Modèle de réponse d'erreur standardisé pour l'API REST
 */
public class ErrorResponse {
    @JsonbProperty("timestamp")
    private LocalDateTime timestamp;
    
    @JsonbProperty("status")
    private int status;
    
    @JsonbProperty("error")
    private String error;
    
    @JsonbProperty("errorCode")
    private String errorCode;
    
    @JsonbProperty("message")
    private String message;
    
    @JsonbProperty("path")
    private String path;

    // Constructeur par défaut
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    // Constructeur complet
    public ErrorResponse(int status, String error, String errorCode, String message, String path) {
        this();
        this.status = status;
        this.error = error;
        this.errorCode = errorCode;
        this.message = message;
        this.path = path;
    }

    // Getters et Setters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}