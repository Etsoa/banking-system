package com.example.serveurcomptecourant.exceptions;

/**
 * Exception pour les erreurs liées aux comptes
 */
public class CompteCourantException extends BankingException {

    public CompteCourantException(String message) {
        super("COMPTE_ERROR", message);
    }

    public CompteCourantException(String message, Throwable cause) {
        super("COMPTE_ERROR", message, cause);
    }

    public CompteCourantException(String errorCode, String message) {
        super(errorCode, message);
    }

    public CompteCourantException(String errorCode, String message, Object... parameters) {
        super(errorCode, message, parameters);
    }

    // Exceptions spécifiques pour les comptes
    public static class CompteNotFoundException extends CompteCourantException {
        public CompteNotFoundException(Integer compteId) {
            super("COMPTE_NOT_FOUND", "Compte introuvable", compteId);
        }
    }

    public static class SoldeInsuffisantException extends CompteCourantException {
        public SoldeInsuffisantException(Integer compteId, Double soldeActuel, Double montantRequis) {
            super("SOLDE_INSUFFISANT", "Solde insuffisant", compteId, soldeActuel, montantRequis);
        }
    }

    public static class MontantInvalideException extends CompteCourantException {
        public MontantInvalideException(Double montant) {
            super("MONTANT_INVALIDE", "Montant invalide", montant);
        }
    }
}