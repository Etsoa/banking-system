package com.example.serveurcomptecourant.controllers;

import java.math.BigDecimal;
import java.util.List;

import com.example.serveurcomptecourant.exceptions.CompteCourantException;
import com.example.serveurcomptecourant.models.Transaction;
import com.example.serveurcomptecourant.models.TransactionAvecFrais;
import com.example.serveurcomptecourant.models.TypeTransaction;
import com.example.serveurcomptecourant.services.TransactionService;

import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/compte-courant/transactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionController {
    
    @EJB
    private TransactionService transactionService;

    @GET
    @Path("/compte/{compteId}")
    public List<Transaction> getTransactionsByCompte(@PathParam("compteId") String compteId) throws CompteCourantException {
        return transactionService.getTransactionsByCompte(compteId);
    }

    @GET
    @Path("/compte/{compteId}/avec-frais")
    public List<TransactionAvecFrais> getTransactionsByCompteAvecFrais(@PathParam("compteId") String compteId) throws CompteCourantException {
        return transactionService.getTransactionsByCompteAvecFrais(compteId);
    }

    @GET
    @Path("/compte/{compteId}/type/{typeId}")
    public List<Transaction> getTransactionsByCompteAndType(@PathParam("compteId") String compteId, @PathParam("typeId") Integer typeId) throws CompteCourantException {
        return transactionService.getTransactionsByCompteAndType(compteId, typeId);
    }

    @GET
    @Path("/compte/{compteId}/type/{typeId}/avec-frais")
    public List<TransactionAvecFrais> getTransactionsByCompteAndTypeAvecFrais(@PathParam("compteId") String compteId, @PathParam("typeId") Integer typeId) throws CompteCourantException {
        return transactionService.getTransactionsByCompteAndTypeAvecFrais(compteId, typeId);
    }

    @GET
    @Path("/types")
    public List<TypeTransaction> getAllTypesTransaction() throws CompteCourantException {
        return transactionService.getTypesTransactionActifs();
    }

    @POST
    public Transaction createTransaction(Transaction transaction, @QueryParam("revenu") BigDecimal revenu) throws CompteCourantException {
        return transactionService.createTransaction(transaction, revenu);
    }
}