package com.example.serveurcomptecourant.controllers;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.example.serveurcomptecourant.models.Utilisateur;
import com.example.serveurcomptecourant.services.UtilisateurService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {
    
    @Context
    private HttpServletRequest httpRequest;
    
    /**
     * Obtenir ou créer un UtilisateurService via JNDI pour la session
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

    /**
     * Endpoint de connexion
     */
    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        try {
            if (request == null || request.nomUtilisateur == null || request.motDePasse == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Nom d'utilisateur et mot de passe requis\"}")
                    .build();
            }
            
            // Obtenir un nouveau service utilisateur
            UtilisateurService utilisateurService = getUtilisateurService();
            
            boolean success = utilisateurService.login(request.nomUtilisateur, request.motDePasse);
            
            if (success) {
                // Créer une session HTTP pour générer le cookie JSESSIONID
                HttpSession session = httpRequest.getSession(true);
                session.setAttribute("authenticated", true);
                session.setAttribute("username", request.nomUtilisateur);
                // Stocker le bean @Stateful dans la session
                session.setAttribute("utilisateurService", utilisateurService);
                
                Utilisateur utilisateur = utilisateurService.getUtilisateurConnecte();
                LoginResponse response = new LoginResponse();
                response.success = true;
                response.utilisateur = utilisateur;
                response.message = "Connexion réussie";
                
                return Response.ok(response).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Nom d'utilisateur ou mot de passe incorrect\"}")
                    .build();
            }
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"Erreur lors de la connexion: " + e.getMessage() + "\"}")
                .build();
        }
    }

    /**
     * Endpoint de déconnexion
     */
    @POST
    @Path("/logout")
    public Response logout() {
        try {
            UtilisateurService utilisateurService = getUtilisateurService();
            utilisateurService.logout();
            
            // Invalider la session HTTP
            HttpSession session = httpRequest.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            
            return Response.ok("{\"message\":\"Déconnexion réussie\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"Erreur lors de la déconnexion: " + e.getMessage() + "\"}")
                .build();
        }
    }

    /**
     * Endpoint pour récupérer l'utilisateur connecté
     */
    @GET
    @Path("/current")
    public Response getCurrentUser() {
        try {
            UtilisateurService utilisateurService = getUtilisateurService();
            
            if (!utilisateurService.estConnecte()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Aucun utilisateur connecté\"}")
                    .build();
            }
            
            Utilisateur utilisateur = utilisateurService.getUtilisateurConnecte();
            return Response.ok(utilisateur).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"Erreur lors de la récupération de l'utilisateur: " + e.getMessage() + "\"}")
                .build();
        }
    }

    // ===== Classes internes pour les requêtes/réponses =====

    public static class LoginRequest {
        public String nomUtilisateur;
        public String motDePasse;
    }

    public static class LoginResponse {
        public boolean success;
        public Utilisateur utilisateur;
        public String message;
    }
}