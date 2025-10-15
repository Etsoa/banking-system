package com.example.serveurcomptecourant.exceptions;

/**
 * Exception pour les erreurs métier spécifiques du CompteCourant
 */
public class CompteCourantBusinessException extends CompteCourantException {

    public CompteCourantBusinessException(String message) {
        super(message, "BUSINESS_ERROR", 400);
    }

    public CompteCourantBusinessException(String message, String errorCode) {
        super(message, errorCode, 400);
    }

    // Exceptions spécifiques pour les règles métier
    public static class CompteNotFoundException extends CompteCourantBusinessException {
        public CompteNotFoundException(Long compteId) {
            super("Compte avec l'ID " + compteId + " introuvable", "COMPTE_NOT_FOUND");
        }
    }

    public static class SoldeInsuffisantException extends CompteCourantBusinessException {
        public SoldeInsuffisantException(double soldeActuel, double montantDemande) {
            super(String.format("Solde insuffisant. Solde actuel: %.2f, Montant demandé: %.2f", 
                  soldeActuel, montantDemande), "SOLDE_INSUFFISANT");
        }
    }

    public static class MontantInvalideException extends CompteCourantBusinessException {
        public MontantInvalideException(double montant) {
            super("Montant invalide: " + montant + ". Le montant doit être positif.", "MONTANT_INVALIDE");
        }
    }

    public static class ClientInexistantException extends CompteCourantBusinessException {
        public ClientInexistantException(Long clientId) {
            super("Client avec l'ID " + clientId + " n'existe pas", "CLIENT_INEXISTANT");
        }
    }

    public static class CompteDejaExistantException extends CompteCourantBusinessException {
        public CompteDejaExistantException(String numeroCompte) {
            super("Un compte avec le numéro " + numeroCompte + " existe déjà", "COMPTE_DEJA_EXISTANT");
        }
    }
}