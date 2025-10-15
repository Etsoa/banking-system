package com.example.centralizer.services;

import com.example.centralizer.models.exceptions.ServerErrorResponse;
import com.example.centralizer.exceptions.ServerApplicationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import java.util.logging.Logger;

/**
 * Service pour gérer les exceptions provenant des serveurs d'applications
 */
@Service
public class ExceptionHandlingService {
    private static final Logger LOGGER = Logger.getLogger(ExceptionHandlingService.class.getName());
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Traite les exceptions HTTP provenant des appels aux serveurs d'applications
     */
    public void handleServerException(RestClientException exception, String serverName) {
        if (exception instanceof HttpClientErrorException) {
            handleHttpClientError((HttpClientErrorException) exception, serverName);
        } else if (exception instanceof HttpServerErrorException) {
            handleHttpServerError((HttpServerErrorException) exception, serverName);
        } else {
            handleGenericRestException(exception, serverName);
        }
    }

    /**
     * Traite les erreurs HTTP 4xx (erreurs client)
     */
    private void handleHttpClientError(HttpClientErrorException exception, String serverName) {
        try {
            String responseBody = exception.getResponseBodyAsString();
            ServerErrorResponse errorResponse = objectMapper.readValue(responseBody, ServerErrorResponse.class);
            
            String message = String.format("Erreur du serveur %s: %s", serverName, errorResponse.getMessage());
            LOGGER.warning(message);
            
            throw new ServerApplicationException(
                errorResponse.getMessage(),
                errorResponse.getErrorCode(),
                errorResponse.getStatus(),
                errorResponse.getPath()
            );
        } catch (Exception e) {
            // Si on ne peut pas parser la réponse, utiliser l'exception d'origine
            String message = String.format("Erreur du serveur %s: %s", serverName, exception.getMessage());
            LOGGER.warning(message);
            
            throw new ServerApplicationException(
                exception.getMessage(),
                "PARSE_ERROR",
                exception.getStatusCode().value(),
                "unknown"
            );
        }
    }

    /**
     * Traite les erreurs HTTP 5xx (erreurs serveur)
     */
    private void handleHttpServerError(HttpServerErrorException exception, String serverName) {
        try {
            String responseBody = exception.getResponseBodyAsString();
            ServerErrorResponse errorResponse = objectMapper.readValue(responseBody, ServerErrorResponse.class);
            
            String message = String.format("Erreur interne du serveur %s: %s", serverName, errorResponse.getMessage());
            LOGGER.severe(message);
            
            throw new ServerApplicationException(
                errorResponse.getMessage(),
                errorResponse.getErrorCode(),
                errorResponse.getStatus(),
                errorResponse.getPath()
            );
        } catch (Exception e) {
            String message = String.format("Erreur interne du serveur %s: %s", serverName, exception.getMessage());
            LOGGER.severe(message);
            
            throw new ServerApplicationException(
                exception.getMessage(),
                "SERVER_ERROR",
                exception.getStatusCode().value(),
                "unknown"
            );
        }
    }

    /**
     * Traite les autres exceptions REST
     */
    private void handleGenericRestException(RestClientException exception, String serverName) {
        String message = String.format("Erreur de communication avec le serveur %s: %s", serverName, exception.getMessage());
        LOGGER.severe(message);
        
        throw new ServerApplicationException(
            exception.getMessage(),
            "COMMUNICATION_ERROR",
            HttpStatus.SERVICE_UNAVAILABLE.value(),
            "unknown",
            exception
        );
    }

    /**
     * Convertit une ServerApplicationException en message d'erreur utilisateur
     */
    public String getUserFriendlyMessage(ServerApplicationException exception) {
        switch (exception.getErrorCode()) {
            // Erreurs CompteCourant
            case "COMPTE_NOT_FOUND":
                return "Le compte demandé n'existe pas.";
            case "CLIENT_INEXISTANT":
                return "Le client spécifié n'existe pas.";
            case "SOLDE_INSUFFISANT":
                return "Solde insuffisant pour effectuer cette opération.";
            case "MONTANT_INVALIDE":
                return "Le montant saisi est invalide.";
            case "COMPTE_DEJA_EXISTANT":
                return "Un compte avec ce numéro existe déjà.";
            
            // Erreurs Prêt
            case "PRET_NOT_FOUND":
                return "Le prêt demandé n'existe pas.";
            case "MONTANT_PRET_INVALIDE":
                return "Le montant du prêt est invalide.";
            case "DUREE_PRET_INVALIDE":
                return "La durée du prêt est invalide.";
            case "TAUX_INTERET_INVALIDE":
                return "Le taux d'intérêt est invalide.";
            case "PRET_DEJA_REMBOURSE":
                return "Ce prêt est déjà totalement remboursé.";
            case "MONTANT_REMBOURSEMENT_INVALIDE":
                return "Le montant de remboursement est invalide.";
            
            // Erreurs CompteDepot
            case "COMPTE_DEPOT_NOT_FOUND":
                return "Le compte dépôt demandé n'existe pas.";
            case "MONTANT_DEPOT_INVALIDE":
                return "Le montant du dépôt est invalide.";
            case "MONTANT_RETRAIT_INVALIDE":
                return "Le montant du retrait est invalide.";
            case "SOLDE_DEPOT_INSUFFISANT":
                return "Solde insuffisant pour effectuer ce retrait.";
            
            // Erreurs génériques
            case "COMMUNICATION_ERROR":
                return "Erreur de communication avec le serveur. Veuillez réessayer.";
            case "SERVER_ERROR":
                return "Erreur interne du serveur. Veuillez contacter l'administrateur.";
            default:
                return "Une erreur s'est produite: " + exception.getMessage();
        }
    }
}