package com.example.centralizer.ejb;

import com.example.centralizer.dto.LoginRequest;
import com.example.centralizer.dto.LoginResponse;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateful;
import java.util.logging.Logger;

/**
 * Service EJB Stateful pour l'authentification
 */
@Stateful
public class AuthenticationServiceImpl {
    private static final Logger LOGGER = Logger.getLogger(AuthenticationServiceImpl.class.getName());
    
    @EJB
    private CompteCourantServiceImpl compteCourantService;
    
    private Integer currentUserId;
    private String currentUsername;
    private boolean authenticated = false;
    
    public LoginResponse login(LoginRequest request) {
        try {
            // Appeler le serveur de compte courant via CompteCourantService
            // Pour l'instant, utilisons une authentification simple
            // TODO: Implémenter la vraie logique avec ServeurCompteCourant
            
            if (request.getNomUtilisateur() != null && request.getMotDePasse() != null) {
                this.currentUsername = request.getNomUtilisateur();
                this.currentUserId = 1; // Mock - devrait être récupéré du serveur
                this.authenticated = true;
                
                LOGGER.info("Utilisateur " + currentUsername + " authentifié avec succès");
                
                LoginResponse response = new LoginResponse(true, "Authentification réussie");
                response.setIdUtilisateur(currentUserId);
                response.setNomUtilisateur(currentUsername);
                return response;
            }
            
            return new LoginResponse(false, "Nom d'utilisateur ou mot de passe invalide");
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de l'authentification: " + e.getMessage());
            this.authenticated = false;
            this.currentUsername = null;
            this.currentUserId = null;
            return new LoginResponse(false, "Erreur d'authentification: " + e.getMessage());
        }
    }
    
    public void logout() {
        try {
            if (authenticated) {
                LOGGER.info("Utilisateur " + currentUsername + " déconnecté");
            }
        } finally {
            this.authenticated = false;
            this.currentUsername = null;
            this.currentUserId = null;
        }
    }
    
    public boolean isAuthenticated() {
        return authenticated;
    }
    
    public Integer getCurrentUserId() {
        return currentUserId;
    }
    
    public String getCurrentUsername() {
        return currentUsername;
    }
}
