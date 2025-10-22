package com.example.serveurcomptecourant.controllers;

import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.example.serveurcomptecourant.exceptions.SecurityException;
import com.example.serveurcomptecourant.exceptions.TransactionException;
import com.example.serveurcomptecourant.models.StatutTransaction;
import com.example.serveurcomptecourant.models.Transaction;
import com.example.serveurcomptecourant.services.TransactionService;
import com.example.serveurcomptecourant.services.UtilisateurService;

import jakarta.ejb.EJB;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/transactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionController {
    
    @EJB
    private TransactionService transactionService;
    
    @Context
    private HttpServletRequest httpRequest;
    
    /**
     * Obtenir le UtilisateurService depuis la session
     */
    private UtilisateurService getUtilisateurService() throws NamingException {
        HttpSession session = httpRequest.getSession(false);
        
        // Essayer de récupérer le service depuis la session
        if (session != null) {
            UtilisateurService service = (UtilisateurService) session.getAttribute("utilisateurService");
            if (service != null) {
                return service;
            }
        }
        
        // Créer un nouveau service via JNDI
        InitialContext ctx = new InitialContext();
        return (UtilisateurService) ctx.lookup("java:module/UtilisateurService");
    }

    @GET
    public Response getAllTransactions() {
        try {
            // Injecter le UtilisateurService de la session dans le service
            transactionService.setUtilisateurService(getUtilisateurService());
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
    @Path("/compte/{compteId}")
    public Response getTransactionsByCompte(@PathParam("compteId") Integer compteId) {
        try {
            transactionService.setUtilisateurService(getUtilisateurService());
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
    @Path("/statut/{statutTransaction}")
    public Response getTransactionsByStatut(@PathParam("statutTransaction") String statutTransaction) {
        try {
            transactionService.setUtilisateurService(getUtilisateurService());
            StatutTransaction statut = StatutTransaction.valueOf(statutTransaction);
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

    @POST
    @Path("/demander")
    public Response demanderTransaction(Transaction transaction) {
        try {
            transactionService.setUtilisateurService(getUtilisateurService());
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
    public Response validerTransaction(@PathParam("id") Integer id) {
        try {
            transactionService.setUtilisateurService(getUtilisateurService());
            // Valider = approuver à true
            Transaction transaction = transactionService.validerTransaction(id, true);
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
            transactionService.setUtilisateurService(getUtilisateurService());
            // Refuser = approuver à false
            Transaction transaction = transactionService.validerTransaction(id, false);
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
}