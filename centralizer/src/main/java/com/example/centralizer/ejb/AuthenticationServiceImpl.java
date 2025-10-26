package com.example.centralizer.ejb;

import java.io.Serializable;
import java.util.logging.Logger;

import com.example.centralizer.dto.comptecourant.LoginResponse;
import com.example.centralizer.dto.comptecourant.SessionUtilisateur;
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
    
    private Object utilisateurServiceRemote;
    private SessionUtilisateur sessionUtilisateur;  // Session complète de l'utilisateur
    private boolean authenticated = false;
    
    /**
     * Initialise le service EJB local via JNDI
     * Format JNDI pour EJB local dans WildFly: java:global/<module-name>/<bean-name>!<interface>
     * Pour Stateful beans, pas de suffixe spécial dans le lookup
     */
    private void initializeRemoteService() {
        if (utilisateurServiceRemote == null) {
            try {
                InitialContext ctx = new InitialContext();
                // Chemin JNDI local: java:global/comptecourant + bean UtilisateurServiceBean
                // Pour Stateful, on crée une session via ejb:
                String jndiPath = "java:global/comptecourant/UtilisateurServiceBean!com.example.comptecourant.ejb.UtilisateurServiceRemote";
                LOGGER.info("Tentative lookup JNDI local: " + jndiPath);
                utilisateurServiceRemote = ctx.lookup(jndiPath);
                LOGGER.info("UtilisateurServiceRemote initialisé avec succès");
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
            
            // Utiliser la réflexion pour appeler login(String, String)
            boolean result = (boolean) utilisateurServiceRemote.getClass()
                .getMethod("login", String.class, String.class)
                .invoke(utilisateurServiceRemote, nomUtilisateur, motDePasse);
            
            if (result) {
                // Récupérer les informations complètes de l'utilisateur connecté
                // Utiliser la réflexion pour appeler getUtilisateurConnecte()
                Object utilisateur = utilisateurServiceRemote.getClass()
                    .getMethod("getUtilisateurConnecte")
                    .invoke(utilisateurServiceRemote);
                
                if (utilisateur != null) {
                    Integer idUtilisateur = null;
                    String nomUtilisateurConnecte = null;
                    Integer roleUtilisateur = null;
                    
                    Object idObj = utilisateur.getClass().getMethod("getIdUtilisateur").invoke(utilisateur);
                    Object nomObj = utilisateur.getClass().getMethod("getNomUtilisateur").invoke(utilisateur);
                    Object roleObj = utilisateur.getClass().getMethod("getRoleUtilisateur").invoke(utilisateur);
                    
                    if (idObj != null) idUtilisateur = ((Number) idObj).intValue();
                    if (nomObj != null) nomUtilisateurConnecte = nomObj.toString();
                    if (roleObj != null && roleObj instanceof Number) roleUtilisateur = ((Number) roleObj).intValue();
                    
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
                // Utiliser la réflexion pour appeler logout()
                utilisateurServiceRemote.getClass()
                    .getMethod("logout")
                    .invoke(utilisateurServiceRemote);
                
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
            // Utiliser la réflexion pour appeler estConnecte()
            return (boolean) utilisateurServiceRemote.getClass()
                .getMethod("estConnecte")
                .invoke(utilisateurServiceRemote);
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
            
            // Utiliser la réflexion pour appeler aAutorisationPour(String, String)
            return (boolean) utilisateurServiceRemote.getClass()
                .getMethod("aAutorisationPour", String.class, String.class)
                .invoke(utilisateurServiceRemote, nomTable, nomAction);
        } catch (Exception e) {
            LOGGER.warning("Erreur lors de la vérification d'autorisation: " + e.getMessage());
            return false;
        }
    }
}
