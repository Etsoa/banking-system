package com.example.serveurcomptecourant.controllers;

import com.example.serveurcomptecourant.models.Transfert;
import com.example.serveurcomptecourant.services.TransactionService;
import com.example.serveurcomptecourant.services.TransfertService;
import com.example.serveurcomptecourant.exceptions.CompteCourantException;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDateTime;
import java.util.List;

@Path("/compte-courant/transferts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransfertController {
    
    @EJB
    private TransfertService transfertService;

    @EJB
    private TransactionService transactionService;

    @GET
    public List<Transfert> getAllTransferts() throws CompteCourantException {
        return transfertService.getAllTransferts();
    }

    @GET
    @Path("/compte/{compteId}")
    public List<Transfert> getTransfertsByCompte(@PathParam("compteId") String compteId) throws CompteCourantException {
        return transfertService.getTransfertsByCompte(compteId);
    }
    
    @POST
    public Transfert createTransfert(Transfert transfert) throws CompteCourantException {
        // Convertir LocalDate en LocalDateTime pour le service
        LocalDateTime dateTime = transfert.getDateTransfert().atStartOfDay();
        
        return transactionService.createTransfert(
            transfert.getEnvoyer(), 
            transfert.getReceveur(), 
            transfert.getMontant(), 
            dateTime
        );
    }
}