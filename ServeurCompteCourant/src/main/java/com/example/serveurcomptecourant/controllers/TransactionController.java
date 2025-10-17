package com.example.serveurcomptecourant.controllers;

import com.example.serveurcomptecourant.models.Transaction;
import com.example.serveurcomptecourant.models.TypeTransaction;
import com.example.serveurcomptecourant.services.TransactionService;
import com.example.serveurcomptecourant.exceptions.CompteCourantException;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/transactions")
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
    @Path("/compte/{compteId}/type/{typeId}")
    public List<Transaction> getTransactionsByCompteAndType(@PathParam("compteId") String compteId, @PathParam("typeId") Integer typeId) throws CompteCourantException {
        return transactionService.getTransactionsByCompteAndType(compteId, typeId);
    }

    @GET
    @Path("/types")
    public List<TypeTransaction> getAllTypesTransaction() throws CompteCourantException {
        return transactionService.getAllTypesTransaction();
    }

    @POST
    public Transaction createTransaction(Transaction transaction) throws CompteCourantException {
        return transactionService.createTransaction(transaction);
    }

    @POST
    @Path("/transfert/{compteEnvoyeur}/{compteReceveur}/{montant}")
    public com.example.serveurcomptecourant.models.Transfert createTransfert(
            @PathParam("compteEnvoyeur") String compteEnvoyeur,
            @PathParam("compteReceveur") String compteReceveur,
            @PathParam("montant") java.math.BigDecimal montant) throws CompteCourantException {
        return transactionService.createTransfert(compteEnvoyeur, compteReceveur, montant);
    }
}