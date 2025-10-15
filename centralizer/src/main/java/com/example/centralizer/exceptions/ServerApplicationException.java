package com.example.centralizer.exceptions;

/**
 * Exception pour les erreurs provenant des serveurs d'applications
 */
public class ServerApplicationException extends RuntimeException {
    private final String errorCode;
    private final int httpStatus;
    private final String serverPath;

    public ServerApplicationException(String message, String errorCode, int httpStatus, String serverPath) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.serverPath = serverPath;
    }

    public ServerApplicationException(String message, String errorCode, int httpStatus, String serverPath, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.serverPath = serverPath;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getServerPath() {
        return serverPath;
    }

    /**
     * Méthode utilitaire pour formater le message d'erreur complet
     */
    public String getFormattedMessage() {
        return String.format("[%s] %s (Code: %s, Statut: %d, Chemin: %s)", 
                             "ERREUR SERVEUR", getMessage(), errorCode, httpStatus, serverPath);
    }
}