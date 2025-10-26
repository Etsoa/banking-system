package com.example.centralizer.servlets;

import com.example.centralizer.dto.comptecourant.SessionUtilisateur;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Utilitaire pour gérer l'accès à la session utilisateur HTTP
 * Fournit des méthodes helper pour vérifier l'authentification et accéder à la SessionUtilisateur
 */
public class SessionManager {
    private static final String SESSION_UTILISATEUR_KEY = "sessionUtilisateur";
    
    /**
     * Récupère la SessionUtilisateur depuis la session HTTP
     */
    public static SessionUtilisateur getSessionUtilisateur(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (SessionUtilisateur) session.getAttribute(SESSION_UTILISATEUR_KEY);
    }
    
    /**
     * Vérifie si l'utilisateur est authentifié
     */
    public static boolean isAuthenticated(HttpServletRequest request) {
        return getSessionUtilisateur(request) != null;
    }
    
    /**
     * Récupère l'ID de l'utilisateur actuellement connecté
     */
    public static Integer getCurrentUserId(HttpServletRequest request) {
        SessionUtilisateur session = getSessionUtilisateur(request);
        return session != null ? session.getIdUtilisateur() : null;
    }
    
    /**
     * Récupère le nom d'utilisateur actuellement connecté
     */
    public static String getCurrentUsername(HttpServletRequest request) {
        SessionUtilisateur session = getSessionUtilisateur(request);
        return session != null ? session.getNomUtilisateur() : null;
    }
    
    /**
     * Récupère l'ID du rôle de l'utilisateur actuellement connecté
     */
    public static Integer getCurrentUserRoleId(HttpServletRequest request) {
        SessionUtilisateur session = getSessionUtilisateur(request);
        return session != null ? session.getRoleUtilisateur() : null;
    }
    
    /**
     * Vérifie si l'utilisateur connecté a une permission spécifique
     */
    public static boolean hasPermission(HttpServletRequest request, String nomTable, String nomAction) {
        SessionUtilisateur session = getSessionUtilisateur(request);
        if (session == null) {
            return false;
        }
        return session.aAutorisationPour(nomTable, nomAction);
    }
    
    /**
     * Invalide la session (déconnexion)
     */
    public static void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
