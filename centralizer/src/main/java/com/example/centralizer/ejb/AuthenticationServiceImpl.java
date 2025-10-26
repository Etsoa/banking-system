package com.example.centralizer.ejb;

import java.io.Serializable;
import java.util.logging.Logger;

import com.example.centralizer.dto.comptecourant.LoginResponse;
import com.example.centralizer.dto.comptecourant.SessionUtilisateur;
import com.example.comptecourant.ejb.UtilisateurServiceRemote;
import com.example.centralizer.dto.comptecourant.Utilisateur;
import jakarta.ejb.Stateful;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Service EJB Stateful pour l'authentification
 * Gère la session utilisateur complète avec rôles et actions/permissions
 * Utilise JNDI lookup pour accéder aux services EJB distants du module comptecourant
 */
@Stateful
public class AuthenticationServiceImpl implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AuthenticationServiceImpl.class.getName());
    
    private UtilisateurServiceRemote utilisateurServiceRemote;
    private SessionUtilisateur sessionUtilisateur;  // Session complète de l'utilisateur
    private boolean authenticated = false;
    
    /**
     * Initialise le service EJB distant via JNDI
     * Format JNDI pour EJB distant: ejb:<app-name>/<module-name>/<bean-name>!<interface>
     * Pour Stateful beans, pas de suffixe spécial dans le lookup
     */
    private void initializeRemoteService() {
        if (utilisateurServiceRemote == null) {
            try {
                InitialContext ctx = new InitialContext();
                // Chemin JNDI pour EJB déployé dans un JAR séparé
                String jndiPath = "java:jboss/exported/comptecourant/UtilisateurServiceBean!com.example.comptecourant.ejb.UtilisateurServiceRemote";
                LOGGER.info("Tentative lookup JNDI distant: " + jndiPath);
                utilisateurServiceRemote = (UtilisateurServiceRemote) ctx.lookup(jndiPath);
                LOGGER.info("UtilisateurServiceRemote distant initialisé avec succès");
            } catch (NamingException e) {
                LOGGER.severe("Erreur lors du lookup du service EJB distant: " + e.getMessage());
                throw new RuntimeException("Impossible de localiser le service EJB distant", e);
            }
        }
    }
    
    /**
     * Authentification auprès du serveur de compte courant
     * Crée et populate une SessionUtilisateur complète avec tous les rôles et actions/permissions
     */
    public LoginResponse login(String nomUtilisateur, String motDePasse) {
        try {
            initializeRemoteService();
            LOGGER.info("Tentative d'authentification pour: " + nomUtilisateur);
            
            boolean result = utilisateurServiceRemote.login(nomUtilisateur, motDePasse);
            
            if (result) {
                // Récupérer les informations complètes de l'utilisateur connecté
                Utilisateur utilisateur = utilisateurServiceRemote.getUtilisateurConnecte();
                
                if (utilisateur != null) {
                    Integer idUtilisateur = utilisateur.getIdUtilisateur();
                    String nomUtilisateurConnecte = utilisateur.getNomUtilisateur();
                    Integer roleUtilisateur = utilisateur.getRoleUtilisateur();
                    
                    // Créer la session utilisateur complète
                    sessionUtilisateur = new SessionUtilisateur(
                        idUtilisateur, 
                        nomUtilisateurConnecte, 
                        roleUtilisateur
                    );
                    
                    // TODO: Récupérer les ActionRoles pour ce rôle depuis le serveur
                    // sessionUtilisateur.setActionsRoles(fetchActionRolesForRole(roleUtilisateur));
                    
                    authenticated = true;
                    LOGGER.info("Authentification réussie pour: " + nomUtilisateurConnecte + " (ID: " + idUtilisateur + ", Rôle: " + roleUtilisateur + ")");
                    
                    LoginResponse response = new LoginResponse(true, "Authentification réussie");
                    response.setIdUtilisateur(idUtilisateur);
                    response.setNomUtilisateur(nomUtilisateurConnecte);
                    return response;
                }
            }
            
            LOGGER.warning("Échec d'authentification pour: " + nomUtilisateur);
            return new LoginResponse(false, "Nom d'utilisateur ou mot de passe invalide");
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de l'authentification: " + e.getMessage());
            sessionUtilisateur = null;
            authenticated = false;
            return new LoginResponse(false, "Erreur d'authentification: " + e.getMessage());
        }
    }
    
    /**
     * Déconnexion de l'utilisateur
     */
    public void logout() {
        try {
            if (sessionUtilisateur != null) {
                initializeRemoteService();
                utilisateurServiceRemote.logout();
                
                LOGGER.info("Utilisateur " + sessionUtilisateur.getNomUtilisateur() + " déconnecté");
            }
        } catch (Exception e) {
            LOGGER.warning("Erreur lors de la déconnexion: " + e.getMessage());
        } finally {
            sessionUtilisateur = null;
            authenticated = false;
        }
    }
    
    /**
     * Récupère l'utilisateur actuellement connecté
     */
    public LoginResponse getCurrentUser() {
        if (sessionUtilisateur == null || !authenticated) {
            return new LoginResponse(false, "Aucun utilisateur connecté");
        }
        
        LoginResponse response = new LoginResponse(true, "Utilisateur récupéré");
        response.setIdUtilisateur(sessionUtilisateur.getIdUtilisateur());
        response.setNomUtilisateur(sessionUtilisateur.getNomUtilisateur());
        return response;
    }
    
    /**
     * Vérifie si l'utilisateur est authentifié
     */
    public boolean isAuthenticated() {
        try {
            if (!authenticated || sessionUtilisateur == null) {
                return false;
            }
            
            initializeRemoteService();
            return utilisateurServiceRemote.estConnecte();
        } catch (Exception e) {
            LOGGER.warning("Erreur lors de la vérification de connexion: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Retourne la session utilisateur complète
     */
    public SessionUtilisateur getSessionUtilisateur() {
        return sessionUtilisateur;
    }
    
    /**
     * Retourne l'ID de l'utilisateur actuellement connecté
     */
    public Integer getCurrentUserId() {
        return sessionUtilisateur != null ? sessionUtilisateur.getIdUtilisateur() : null;
    }
    
    /**
     * Retourne le nom d'utilisateur actuellement connecté
     */
    public String getCurrentUsername() {
        return sessionUtilisateur != null ? sessionUtilisateur.getNomUtilisateur() : null;
    }
    
    /**
     * Retourne l'ID du rôle de l'utilisateur actuellement connecté
     */
    public Integer getCurrentUserRoleId() {
        return sessionUtilisateur != null ? sessionUtilisateur.getRoleUtilisateur() : null;
    }
    
    /**
     * Vérifie si l'utilisateur connecté a une action/permission spécifique sur une table
     */
    public boolean aAutorisationPour(String nomTable, String nomAction) {
        try {
            if (!authenticated || utilisateurServiceRemote == null) {
                return false;
            }
            
            return utilisateurServiceRemote.aAutorisationPour(nomTable, nomAction);
        } catch (Exception e) {
            LOGGER.warning("Erreur lors de la vérification d'autorisation: " + e.getMessage());
            return false;
        }
    }
}
