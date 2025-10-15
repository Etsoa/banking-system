package com.example.serveurpret.exceptions;

/**
 * Exception pour les erreurs métier spécifiques des Prêts
 */
public class PretBusinessException extends PretException {

    public PretBusinessException(String message) {
        super(message, "BUSINESS_ERROR", 400);
    }

    public PretBusinessException(String message, String errorCode) {
        super(message, errorCode, 400);
    }

    // Exceptions spécifiques pour les règles métier des prêts
    public static class PretNotFoundException extends PretBusinessException {
        public PretNotFoundException(Long pretId) {
            super("Prêt avec l'ID " + pretId + " introuvable", "PRET_NOT_FOUND");
        }
    }

    public static class MontantPretInvalideException extends PretBusinessException {
        public MontantPretInvalideException(double montant) {
            super("Montant de prêt invalide: " + montant + ". Le montant doit être positif.", "MONTANT_PRET_INVALIDE");
        }
    }

    public static class DureePretInvalideException extends PretBusinessException {
        public DureePretInvalideException(int duree) {
            super("Durée de prêt invalide: " + duree + " mois. La durée doit être positive.", "DUREE_PRET_INVALIDE");
        }
    }

    public static class TauxInteretInvalideException extends PretBusinessException {
        public TauxInteretInvalideException(double taux) {
            super("Taux d'intérêt invalide: " + taux + "%. Le taux doit être entre 0 et 100.", "TAUX_INTERET_INVALIDE");
        }
    }

    public static class ClientInexistantException extends PretBusinessException {
        public ClientInexistantException(Long clientId) {
            super("Client avec l'ID " + clientId + " n'existe pas", "CLIENT_INEXISTANT");
        }
    }

    public static class PretDejaRembourseException extends PretBusinessException {
        public PretDejaRembourseException(Long pretId) {
            super("Le prêt " + pretId + " est déjà totalement remboursé", "PRET_DEJA_REMBOURSE");
        }
    }

    public static class MontantRemboursementInvalideException extends PretBusinessException {
        public MontantRemboursementInvalideException(double montantRembourse, double montantRestant) {
            super(String.format("Montant de remboursement invalide: %.2f. Le montant restant est de: %.2f", 
                  montantRembourse, montantRestant), "MONTANT_REMBOURSEMENT_INVALIDE");
        }
    }
}