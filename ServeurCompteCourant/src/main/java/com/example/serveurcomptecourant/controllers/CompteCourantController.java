package com.example.serveurcomptecourant.controllers;

import java.math.BigDecimal;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.example.serveurcomptecourant.models.CompteCourant;
import com.example.serveurcomptecourant.services.CompteCourantService;
import com.example.serveurcomptecourant.services.UtilisateurService;

import jakarta.ejb.EJB;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/comptes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CompteCourantController {
    
    @EJB
    private CompteCourantService compteService;
    
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
    public Response getAllComptes() {
        try {
            UtilisateurService utilisateurService = getUtilisateurService();
            utilisateurService.exigerAutorisation("comptes", "read");
            List<CompteCourant> comptes = compteService.getAllComptes();
            return Response.ok(comptes).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getCompteById(@PathParam("id") Integer id) {
        try {
            UtilisateurService utilisateurService = getUtilisateurService();
            utilisateurService.exigerAutorisation("comptes", "read");
            CompteCourant compte = compteService.getCompteById(id);
            return Response.ok(compte).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        }
    }

    @POST
    public Response createCompte(CompteCourant compte) {
        try {
            UtilisateurService utilisateurService = getUtilisateurService();
            utilisateurService.exigerAutorisation("comptes", "create");
            CompteCourant nouveauCompte = compteService.createCompte(compte);
            return Response.status(Response.Status.CREATED).entity(nouveauCompte).build();
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

    @PUT
    @Path("/{id}")
    public Response updateCompte(@PathParam("id") Integer id, CompteCourant compte) {
        try {
            UtilisateurService utilisateurService = getUtilisateurService();
            utilisateurService.exigerAutorisation("comptes", "update");
            compte.setIdCompte(id);
            CompteCourant compteMisAJour = compteService.updateCompte(compte);
            return Response.ok(compteMisAJour).build();
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

    @PUT
    @Path("/{id}/solde")
    public Response updateSolde(@PathParam("id") Integer id, SoldeRequest request) {
        try {
            UtilisateurService utilisateurService = getUtilisateurService();
            utilisateurService.exigerAutorisation("comptes", "update");
            CompteCourant compte = compteService.updateSolde(id, request.nouveauSolde);
            return Response.ok(compte).build();
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

    @DELETE
    @Path("/{id}")
    public Response deleteCompte(@PathParam("id") Integer id) {
        try {
            UtilisateurService utilisateurService = getUtilisateurService();
            utilisateurService.exigerAutorisation("comptes", "delete");
            compteService.deleteCompte(id);
            return Response.ok("{\"message\":\"Compte supprimé avec succès\"}").build();
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

    // Classe interne pour la requête de mise à jour du solde
    public static class SoldeRequest {
        public BigDecimal nouveauSolde;
    }
}
