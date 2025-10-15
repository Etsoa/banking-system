package com.example.serveurcomptecourant.exceptions;

import com.example.serveurcomptecourant.models.ErrorResponse;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Exception Mapper pour gérer automatiquement les exceptions CompteCourant
 * et les convertir en réponses HTTP standardisées
 */
@Provider
public class CompteCourantExceptionMapper implements ExceptionMapper<CompteCourantException> {

    private static final Logger LOGGER = Logger.getLogger(CompteCourantExceptionMapper.class.getName());

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(CompteCourantException exception) {
        // Log de l'exception
        LOGGER.log(Level.WARNING, "Exception métier: " + exception.getMessage(), exception);
        
        // Création de la réponse d'erreur standardisée
        ErrorResponse errorResponse = new ErrorResponse(
            exception.getHttpStatus(),
            getErrorName(exception.getHttpStatus()),
            exception.getErrorCode(),
            exception.getMessage(),
            uriInfo != null ? uriInfo.getPath() : "unknown"
        );

        return Response
                .status(exception.getHttpStatus())
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    /**
     * Obtient le nom de l'erreur HTTP à partir du code de statut
     */
    private String getErrorName(int status) {
        switch (status) {
            case 400:
                return "Bad Request";
            case 404:
                return "Not Found";
            case 409:
                return "Conflict";
            case 422:
                return "Unprocessable Entity";
            case 500:
                return "Internal Server Error";
            default:
                return "Error";
        }
    }
}