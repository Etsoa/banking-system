package com.example.serveurcomptecourant.exceptions;

/**
 * Exception pour les erreurs liées aux transactions
 */
public class TransactionException extends BankingException {

    public TransactionException(String message) {
        super("TRANSACTION_ERROR", message);
    }

    public TransactionException(String message, Throwable cause) {
        super("TRANSACTION_ERROR", message, cause);
    }

    public TransactionException(String errorCode, String message) {
        super(errorCode, message);
    }

    public TransactionException(String errorCode, String message, Object... parameters) {
        super(errorCode, message, parameters);
    }

    // Exceptions spécifiques pour les transactions
    public static class TransactionNotFoundException extends TransactionException {
        public TransactionNotFoundException(Integer transactionId) {
            super("TRANSACTION_NOT_FOUND", "Transaction introuvable", transactionId);
        }
    }

    public static class TransactionStatutInvalideException extends TransactionException {
        public TransactionStatutInvalideException(Integer transactionId, String statutActuel, String statutRequis) {
            super("TRANSACTION_STATUT_INVALIDE", "Statut de transaction invalide", transactionId, statutActuel, statutRequis);
        }
    }

    public static class TransactionNonModifiableException extends TransactionException {
        public TransactionNonModifiableException(Integer transactionId) {
            super("TRANSACTION_NON_MODIFIABLE", "Transaction non modifiable", transactionId);
        }
    }
}