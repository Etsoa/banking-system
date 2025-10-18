package com.example.serveurcomptecourant.controllers;

import com.example.serveurcomptecourant.models.HistoriqueSolde;
import com.example.serveurcomptecourant.services.HistoriqueSoldeService;
import com.example.serveurcomptecourant.exceptions.CompteCourantException;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/compte-courant/historiques-solde")
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
}