package com.example.serveurcomptecourant.exceptions;

/**
 * Exception pour les erreurs d'authentification et d'autorisation
 */
public class SecurityException extends BankingException {

    public SecurityException(String message) {
        super("SECURITY_ERROR", message);
    }

    public SecurityException(String message, Throwable cause) {
        super("SECURITY_ERROR", message, cause);
    }

    public SecurityException(String errorCode, String message) {
        super(errorCode, message);
    }

    public SecurityException(String errorCode, String message, Object... parameters) {
        super(errorCode, message, parameters);
    }

    // Exceptions spécifiques pour la sécurité
    public static class AuthentificationException extends SecurityException {
        public AuthentificationException(String nomUtilisateur) {
            super("AUTH_FAILED", "Échec de l'authentification", nomUtilisateur);
        }
    }

    public static class AutorisationException extends SecurityException {
        public AutorisationException(String nomUtilisateur, String table, String action) {
            super("AUTHORIZATION_DENIED", "Autorisation refusée", nomUtilisateur, table, action);
        }
    }

    public static class SessionExpiredException extends SecurityException {
        public SessionExpiredException() {
            super("SESSION_EXPIRED", "Session expirée");
        }
    }

    public static class UserNotFoundException extends SecurityException {
        public UserNotFoundException(String nomUtilisateur) {
            super("USER_NOT_FOUND", "Utilisateur introuvable", nomUtilisateur);
        }
    }
}