package com.example.serveurcomptecourant.exceptions;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.serveurcomptecourant.exceptions.BankingException;
import com.example.serveurcomptecourant.exceptions.CompteCourantException;
import com.example.serveurcomptecourant.exceptions.SecurityException;
import com.example.serveurcomptecourant.exceptions.TransactionException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Gestionnaire global des exceptions pour les services REST
 */
@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Throwable> {
    
    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionHandler.class.getName());

    @Override
    public Response toResponse(Throwable exception) {
        
        // Logging de l'exception
        LOGGER.log(Level.SEVERE, "Exception interceptée: {0}", exception.getMessage());
        
        // Traitement selon le type d'exception
        if (exception instanceof SecurityException) {
            return handleSecurityException((SecurityException) exception);
        } else if (exception instanceof CompteCourantException) {
            return handleCompteCourantException((CompteCourantException) exception);
        } else if (exception instanceof TransactionException) {
            return handleTransactionException((TransactionException) exception);
        } else if (exception instanceof BankingException) {
            return handleBankingException((BankingException) exception);
        } else if (exception instanceof IllegalArgumentException) {
            return handleIllegalArgumentException((IllegalArgumentException) exception);
        } else if (exception instanceof IllegalStateException) {
            return handleIllegalStateException((IllegalStateException) exception);
        } else {
            return handleGenericException(exception);
        }
    }

    private Response handleSecurityException(SecurityException ex) {
        ErrorResponse error = new ErrorResponse();
        error.errorCode = ex.getErrorCode();
        error.message = ex.getMessage();
        error.parameters = ex.getParameters();
        
        if (ex instanceof SecurityException.AuthentificationException || 
            ex instanceof SecurityException.SessionExpiredException) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(error).build();
        } else if (ex instanceof SecurityException.AutorisationException) {
            return Response.status(Response.Status.FORBIDDEN).entity(error).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).entity(error).build();
        }
    }

    private Response handleCompteCourantException(CompteCourantException ex) {
        ErrorResponse error = new ErrorResponse();
        error.errorCode = ex.getErrorCode();
        error.message = ex.getMessage();
        error.parameters = ex.getParameters();
        
        if (ex instanceof CompteCourantException.CompteNotFoundException) {
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        } else if (ex instanceof CompteCourantException.SoldeInsuffisantException || 
                   ex instanceof CompteCourantException.MontantInvalideException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
    }

    private Response handleTransactionException(TransactionException ex) {
        ErrorResponse error = new ErrorResponse();
        error.errorCode = ex.getErrorCode();
        error.message = ex.getMessage();
        error.parameters = ex.getParameters();
        
        if (ex instanceof TransactionException.TransactionNotFoundException) {
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        } else if (ex instanceof TransactionException.TransactionStatutInvalideException ||
                   ex instanceof TransactionException.TransactionNonModifiableException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
    }

    private Response handleBankingException(BankingException ex) {
        ErrorResponse error = new ErrorResponse();
        error.errorCode = ex.getErrorCode();
        error.message = ex.getMessage();
        error.parameters = ex.getParameters();
        
        return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
    }

    private Response handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse();
        error.errorCode = "INVALID_ARGUMENT";
        error.message = ex.getMessage();
        error.parameters = new Object[0];
        
        return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
    }

    private Response handleIllegalStateException(IllegalStateException ex) {
        ErrorResponse error = new ErrorResponse();
        error.errorCode = "INVALID_STATE";
        error.message = ex.getMessage();
        error.parameters = new Object[0];
        
        return Response.status(Response.Status.CONFLICT).entity(error).build();
    }

    private Response handleGenericException(Throwable ex) {
        ErrorResponse error = new ErrorResponse();
        error.errorCode = "INTERNAL_ERROR";
        error.message = "Une erreur interne s'est produite";
        error.parameters = new Object[0];
        
        // Log complet pour les erreurs inattendues
        LOGGER.log(Level.SEVERE, "Erreur interne inattendue", ex);
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
    }

    /**
     * Classe pour formater les réponses d'erreur
     */
    public static class ErrorResponse {
        public String errorCode;
        public String message;
        public Object[] parameters;
        public long timestamp = System.currentTimeMillis();
    }
}