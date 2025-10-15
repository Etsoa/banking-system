package com.example.serveurcomptecourant.controllers;

import com.example.serveurcomptecourant.models.Transaction;
import com.example.serveurcomptecourant.models.TypeTransaction;
import com.example.serveurcomptecourant.repository.TransactionRepository;
import com.example.serveurcomptecourant.repository.TypeTransactionRepository;
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
    private TransactionRepository transactionRepository;
    
    @EJB
    private TypeTransactionRepository typeTransactionRepository;

    @GET
    @Path("/compte/{compteId}")
    public List<Transaction> getTransactionsByCompte(@PathParam("compteId") int compteId) throws CompteCourantException {
        return transactionRepository.findByCompteId(compteId);
    }

    @GET
    @Path("/compte/{compteId}/type/{typeId}")
    public List<Transaction> getTransactionsByCompteAndType(
            @PathParam("compteId") int compteId,
            @PathParam("typeId") int typeId) throws CompteCourantException {
        return transactionRepository.findByCompteIdAndTypeTransaction(compteId, typeId);
    }

    @GET
    @Path("/types")
    public List<TypeTransaction> getAllTypesTransaction() throws CompteCourantException {
        return typeTransactionRepository.findAll();
    }

    @POST
    public Transaction createTransaction(Transaction transaction) throws CompteCourantException {
        return transactionRepository.save(transaction);
    }
}