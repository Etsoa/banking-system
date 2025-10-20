package com.example.serveurcomptecourant.controllers;

import java.util.List;

import com.example.serveurcomptecourant.models.Direction;
import com.example.serveurcomptecourant.models.Utilisateur;
import com.example.serveurcomptecourant.services.DirectionService;
import com.example.serveurcomptecourant.services.UtilisateurService;

import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    @EJB
    private UtilisateurService utilisateurService;
    
    @EJB
    private DirectionService directionService;

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
            
            boolean success = utilisateurService.login(request.nomUtilisateur, request.motDePasse);
            
            if (success) {
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
            utilisateurService.logout();
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

    /**
     * Endpoint pour vérifier une autorisation
     */
    @GET
    @Path("/check/{table}/{action}")
    public Response checkAuthorization(@PathParam("table") String table, @PathParam("action") String action) {
        try {
            if (!utilisateurService.estConnecte()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"authorized\":false,\"error\":\"Aucun utilisateur connecté\"}")
                    .build();
            }
            
            boolean authorized = utilisateurService.aAutorisationPour(table, action);
            AuthorizationResponse response = new AuthorizationResponse();
            response.authorized = authorized;
            response.table = table;
            response.action = action;
            response.userRole = utilisateurService.getRoleUtilisateurConnecte();
            
            return Response.ok(response).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"Erreur lors de la vérification d'autorisation: " + e.getMessage() + "\"}")
                .build();
        }
    }

    // ===== Gestion des utilisateurs =====

    @GET
    @Path("/users")
    public Response getAllUsers() {
        try {
            List<Utilisateur> utilisateurs = utilisateurService.getAllUtilisateurs();
            return Response.ok(utilisateurs).build();
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
    @Path("/users/{id}")
    public Response getUserById(@PathParam("id") Integer id) {
        try {
            Utilisateur utilisateur = utilisateurService.getUtilisateurById(id);
            return Response.ok(utilisateur).build();
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
    @Path("/users")
    public Response createUser(Utilisateur utilisateur) {
        try {
            Utilisateur nouvelUtilisateur = utilisateurService.createUtilisateur(utilisateur);
            return Response.status(Response.Status.CREATED).entity(nouvelUtilisateur).build();
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
    @Path("/users/{id}")
    public Response updateUser(@PathParam("id") Integer id, Utilisateur utilisateur) {
        try {
            utilisateur.setIdUtilisateur(id);
            Utilisateur utilisateurMisAJour = utilisateurService.updateUtilisateur(utilisateur);
            return Response.ok(utilisateurMisAJour).build();
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
    @Path("/users/{id}")
    public Response deleteUser(@PathParam("id") Integer id) {
        try {
            utilisateurService.deleteUtilisateur(id);
            return Response.ok("{\"message\":\"Utilisateur supprimé avec succès\"}").build();
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

    // ===== Gestion des directions =====

    @GET
    @Path("/directions")
    public Response getAllDirections() {
        try {
            List<Direction> directions = directionService.getAllDirections();
            return Response.ok(directions).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
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

    public static class AuthorizationResponse {
        public boolean authorized;
        public String table;
        public String action;
        public Integer userRole;
    }
}