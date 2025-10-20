package com.example.serveurcomptecourant.controllers;

import java.util.List;

import com.example.serveurcomptecourant.exceptions.SecurityException;
import com.example.serveurcomptecourant.exceptions.TransactionException;
import com.example.serveurcomptecourant.models.Transaction;
import com.example.serveurcomptecourant.services.TransactionService;
import com.example.serveurcomptecourant.services.UtilisateurService;

import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/transactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionController {
    
    @EJB
    private TransactionService transactionService;
    
    @EJB
    private UtilisateurService utilisateurService;

    @GET
    public Response getAllTransactions() {
        try {
            List<Transaction> transactions = transactionService.getAllTransactions();
            return Response.ok(transactions).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"Erreur interne du serveur\"}")
                .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getTransactionById(@PathParam("id") Integer id) {
        try {
            Transaction transaction = transactionService.getTransactionById(id);
            return Response.ok(transaction).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"Erreur interne du serveur\"}")
                .build();
        }
    }

    @GET
    @Path("/compte/{compteId}")
    public Response getTransactionsByCompte(@PathParam("compteId") Integer compteId) {
        try {
            utilisateurService.exigerAutorisation("transactions", "read");
            List<Transaction> transactions = transactionService.getTransactionsByCompte(compteId);
            return Response.ok(transactions).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        }
    }

    @GET
    @Path("/compte/{compteId}/type/{typeTransaction}")
    public Response getTransactionsByCompteAndType(@PathParam("compteId") Integer compteId, 
                                                   @PathParam("typeTransaction") String typeTransaction) {
        try {
            utilisateurService.exigerAutorisation("transactions", "read");
            Transaction.TypeTransaction type = Transaction.TypeTransaction.valueOf(typeTransaction);
            List<Transaction> transactions = transactionService.getTransactionsByCompteAndType(compteId, type);
            return Response.ok(transactions).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        }
    }

    @GET
    @Path("/statut/{statutTransaction}")
    public Response getTransactionsByStatut(@PathParam("statutTransaction") String statutTransaction) {
        try {
            utilisateurService.exigerAutorisation("transactions", "read");
            Transaction.StatutTransaction statut = Transaction.StatutTransaction.valueOf(statutTransaction);
            List<Transaction> transactions = transactionService.getTransactionsByStatut(statut);
            return Response.ok(transactions).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        }
    }

    @GET
    @Path("/compte/{compteId}/statut/{statutTransaction}")
    public Response getTransactionsByCompteAndStatut(@PathParam("compteId") Integer compteId,
                                                     @PathParam("statutTransaction") String statutTransaction) {
        try {
            utilisateurService.exigerAutorisation("transactions", "read");
            Transaction.StatutTransaction statut = Transaction.StatutTransaction.valueOf(statutTransaction);
            List<Transaction> transactions = transactionService.getTransactionsByCompteAndStatut(compteId, statut);
            return Response.ok(transactions).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        }
    }

    @POST
    @Path("/demander")
    public Response demanderTransaction(Transaction transaction) {
        try {
            Transaction nouvelleTransaction = transactionService.demanderTransaction(transaction);
            return Response.status(Response.Status.CREATED).entity(nouvelleTransaction).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (TransactionException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"Erreur interne du serveur\"}")
                .build();
        }
    }

    @PUT
    @Path("/{id}/valider")
    public Response validerTransaction(@PathParam("id") Integer id, 
                                     @QueryParam("approuver") boolean approuver) {
        try {
            Transaction transaction = transactionService.validerTransaction(id, approuver);
            return Response.ok(transaction).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (TransactionException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"Erreur interne du serveur\"}")
                .build();
        }
    }

    @GET
    @Path("/en-attente")
    public Response getTransactionsEnAttente() {
        try {
            List<Transaction> transactions = transactionService.getTransactionsEnAttente();
            return Response.ok(transactions).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (TransactionException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"Erreur interne du serveur\"}")
                .build();
        }
    }

    @GET
    @Path("/peut-valider")
    public Response peutValiderTransactions() {
        try {
            boolean peutValider = transactionService.peutValiderTransactions();
            return Response.ok("{\"peutValider\":" + peutValider + "}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"Erreur interne du serveur\"}")
                .build();
        }
    }

    @PUT
    @Path("/{id}/confirmer")
    public Response confirmerTransaction(@PathParam("id") Integer id) {
        try {
            Transaction transaction = transactionService.confirmerTransaction(id);
            return Response.ok(transaction).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (TransactionException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"Erreur interne du serveur\"}")
                .build();
        }
    }

    @PUT
    @Path("/{id}/refuser")
    public Response refuserTransaction(@PathParam("id") Integer id) {
        try {
            Transaction transaction = transactionService.refuserTransaction(id);
            return Response.ok(transaction).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (TransactionException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"Erreur interne du serveur\"}")
                .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateTransaction(@PathParam("id") Integer id, Transaction transaction) {
        try {
            transaction.setIdTransaction(id);
            Transaction transactionMiseAJour = transactionService.updateTransaction(transaction);
            return Response.ok(transactionMiseAJour).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (TransactionException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"Erreur interne du serveur\"}")
                .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteTransaction(@PathParam("id") Integer id) {
        try {
            transactionService.deleteTransaction(id);
            return Response.ok("{\"message\":\"Transaction supprimée avec succès\"}").build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (TransactionException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"Erreur interne du serveur\"}")
                .build();
        }
    }
}