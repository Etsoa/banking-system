package com.example.serveurcomptecourant.controllers;

import com.example.serveurcomptecourant.models.Transfert;
import com.example.serveurcomptecourant.services.TransfertService;
import com.example.serveurcomptecourant.exceptions.CompteCourantException;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.List;

@Path("/transferts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransfertController {
    
    @EJB
    private TransfertService transfertService;

    @GET
    public List<Transfert> getAllTransferts() throws CompteCourantException {
        return transfertService.getAllTransferts();
    }

    @GET
    @Path("/compte/{compteId}")
    public List<Transfert> getTransfertsByCompte(@PathParam("compteId") String compteId) throws CompteCourantException {
        return transfertService.getTransfertsByCompte(compteId);
    }

    @GET
    @Path("/envoyes/{compteId}")
    public List<Transfert> getTransfertsEnvoyes(@PathParam("compteId") String compteId) throws CompteCourantException {
        return transfertService.getTransfertsEnvoyes(compteId);
    }

    @GET
    @Path("/recus/{compteId}")
    public List<Transfert> getTransfertsRecus(@PathParam("compteId") String compteId) throws CompteCourantException {
        return transfertService.getTransfertsRecus(compteId);
    }

    @GET
    @Path("/total/envoyes/{compteId}")
    public BigDecimal getTotalTransfertsEnvoyes(@PathParam("compteId") String compteId) throws CompteCourantException {
        return transfertService.getTotalTransfertsEnvoyes(compteId);
    }

    @GET
    @Path("/total/recus/{compteId}")
    public BigDecimal getTotalTransfertsRecus(@PathParam("compteId") String compteId) throws CompteCourantException {
        return transfertService.getTotalTransfertsRecus(compteId);
    }

    @GET
    @Path("/{id}")
    public Transfert getTransfertById(@PathParam("id") Integer id) throws CompteCourantException {
        return transfertService.getTransfertById(id);
    }
}