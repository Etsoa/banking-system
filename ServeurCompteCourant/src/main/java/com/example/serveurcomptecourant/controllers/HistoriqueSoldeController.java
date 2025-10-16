package com.example.serveurcomptecourant.controllers;

import com.example.serveurcomptecourant.models.HistoriqueSolde;
import com.example.serveurcomptecourant.services.HistoriqueSoldeService;
import com.example.serveurcomptecourant.exceptions.CompteCourantException;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Path("/historiques-solde")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HistoriqueSoldeController {
    
    @EJB
    private HistoriqueSoldeService historiqueSoldeService;

    @GET
    public List<HistoriqueSolde> getAllHistoriquesSolde() throws CompteCourantException {
        return historiqueSoldeService.getAllHistoriquesSolde();
    }

    @GET
    @Path("/compte/{compteId}")
    public List<HistoriqueSolde> getHistoriqueSoldeByCompte(@PathParam("compteId") String compteId) throws CompteCourantException {
        return historiqueSoldeService.getHistoriqueSoldeByCompte(compteId);
    }

    @GET
    @Path("/dernier/{compteId}")
    public HistoriqueSolde getDernierSolde(@PathParam("compteId") String compteId) throws CompteCourantException {
        return historiqueSoldeService.getDernierSolde(compteId);
    }

    @GET
    @Path("/transaction/{transactionId}")
    public HistoriqueSolde getHistoriqueSoldeByTransaction(@PathParam("transactionId") Integer transactionId) throws CompteCourantException {
        return historiqueSoldeService.getHistoriqueSoldeByTransaction(transactionId);
    }

    @GET
    @Path("/evolution/{compteId}/{dateDebut}/{dateFin}")
    public BigDecimal getEvolutionSolde(
            @PathParam("compteId") String compteId,
            @PathParam("dateDebut") String dateDebut,
            @PathParam("dateFin") String dateFin) throws CompteCourantException {
        
        LocalDateTime debut = LocalDateTime.parse(dateDebut);
        LocalDateTime fin = LocalDateTime.parse(dateFin);
        
        return historiqueSoldeService.getEvolutionSolde(compteId, debut, fin);
    }

    @GET
    @Path("/minimum/{compteId}/{dateDebut}/{dateFin}")
    public BigDecimal getSoldeMinimum(
            @PathParam("compteId") String compteId,
            @PathParam("dateDebut") String dateDebut,
            @PathParam("dateFin") String dateFin) throws CompteCourantException {
        
        LocalDateTime debut = LocalDateTime.parse(dateDebut);
        LocalDateTime fin = LocalDateTime.parse(dateFin);
        
        return historiqueSoldeService.getSoldeMinimum(compteId, debut, fin);
    }

    @GET
    @Path("/maximum/{compteId}/{dateDebut}/{dateFin}")
    public BigDecimal getSoldeMaximum(
            @PathParam("compteId") String compteId,
            @PathParam("dateDebut") String dateDebut,
            @PathParam("dateFin") String dateFin) throws CompteCourantException {
        
        LocalDateTime debut = LocalDateTime.parse(dateDebut);
        LocalDateTime fin = LocalDateTime.parse(dateFin);
        
        return historiqueSoldeService.getSoldeMaximum(compteId, debut, fin);
    }
}