package com.example.centralizer.ejb;

import java.util.logging.Logger;

import com.example.centralizer.dto.LoginRequest;
import com.example.centralizer.dto.LoginResponse;

import jakarta.ejb.Stateful;

/**
 * Service EJB Stateful pour l'authentification
 */
@Stateful
public class AuthenticationServiceImpl {
    private static final Logger LOGGER = Logger.getLogger(AuthenticationServiceImpl.class.getName());
    
    private Integer currentUserId;
    private String currentUsername;
    private boolean authenticated = false;
    
    public LoginResponse login(LoginRequest request) {
        try {
            // Validation simple - l'authentification réelle se fait dans CompteCourantServiceImpl
            if (request.getNomUtilisateur() != null && request.getMotDePasse() != null) {
                this.currentUsername = request.getNomUtilisateur();
                this.currentUserId = request.getIdUtilisateur() != null ? request.getIdUtilisateur() : 1;
                this.authenticated = true;
                
                LOGGER.info("Utilisateur " + currentUsername + " (ID: " + currentUserId + ") session créée");
                
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
