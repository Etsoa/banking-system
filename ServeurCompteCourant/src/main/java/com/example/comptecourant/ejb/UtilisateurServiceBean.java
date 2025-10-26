package com.example.comptecourant.ejb;

import com.example.comptecourant.models.Utilisateur;
import com.example.comptecourant.exceptions.CompteCourantException;
import jakarta.ejb.Stateful;
import jakarta.ejb.Remove;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.NoResultException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Bean Stateful pour la gestion des utilisateurs et authentification.
 * Maintient l'état de session de l'utilisateur connecté.
 * Logique métier basée sur le UtilisateurService original.
 */
@Stateful
public class UtilisateurServiceBean implements UtilisateurServiceRemote {

    private static final Logger LOGGER = Logger.getLogger(UtilisateurServiceBean.class.getName());

    @PersistenceContext(unitName = "CompteCourantPU")
    private EntityManager em;

    // État de session
    private Utilisateur utilisateurConnecte;
    private boolean estConnecte = false;

    /**
     * Authentifie un utilisateur et démarre une session
     */
    @Override
    public boolean login(String nomUtilisateur, String motDePasse) throws CompteCourantException {
        try {
            if (nomUtilisateur == null || nomUtilisateur.trim().isEmpty()) {
                throw new CompteCourantException("Le nom d'utilisateur est obligatoire");
            }
            if (motDePasse == null || motDePasse.trim().isEmpty()) {
                throw new CompteCourantException("Le mot de passe est obligatoire");
            }

            // Rechercher l'utilisateur par nom d'utilisateur
            Utilisateur utilisateur = null;
            try {
                utilisateur = em.createQuery(
                    "SELECT u FROM Utilisateur u WHERE u.nomUtilisateur = :nomUtilisateur",
                    Utilisateur.class
                ).setParameter("nomUtilisateur", nomUtilisateur).getSingleResult();
            } catch (NoResultException e) {
                LOGGER.log(Level.WARNING, "Tentative de connexion avec un nom d'utilisateur inexistant: {0}", nomUtilisateur);
                return false;
            }

            // Vérifier le mot de passe (en production, utiliser un hash)
            if (!motDePasse.equals(utilisateur.getMotDePasse())) {
                LOGGER.log(Level.WARNING, "Tentative de connexion avec un mot de passe incorrect pour: {0}", nomUtilisateur);
                return false;
            }

            // Connexion réussie - stocker en session
            this.utilisateurConnecte = utilisateur;
            this.estConnecte = true;

            LOGGER.log(Level.INFO, "Utilisateur connecté: {0} (Role: {1})",
                new Object[]{nomUtilisateur, utilisateur.getRoleUtilisateur()});
            return true;

        } catch (CompteCourantException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'authentification", e);
            throw new CompteCourantException("Erreur lors de l'authentification: " + e.getMessage(), e);
        }
    }

    /**
     * Déconnecte l'utilisateur et termine la session
     */
    @Remove
    @Override
    public void logout() {
        if (utilisateurConnecte != null) {
            LOGGER.log(Level.INFO, "Déconnexion de l'utilisateur: {0}", utilisateurConnecte.getNomUtilisateur());
        }
        this.utilisateurConnecte = null;
        this.estConnecte = false;
    }

    /**
     * Vérifie si un utilisateur est connecté
     */
    @Override
    public boolean estConnecte() {
        return estConnecte && utilisateurConnecte != null;
    }

    /**
     * Récupère l'utilisateur connecté
     */
    @Override
    public Utilisateur getUtilisateurConnecte() throws CompteCourantException {
        if (!estConnecte()) {
            throw new CompteCourantException("Aucun utilisateur connecté");
        }
        return utilisateurConnecte;
    }

    /**
     * Récupère le rôle de l'utilisateur connecté
     */
    @Override
    public Integer getRoleUtilisateurConnecte() {
        if (!estConnecte()) {
            return null;
        }
        return utilisateurConnecte.getRoleUtilisateur();
    }

    /**
     * Vérifie si l'utilisateur connecté a l'autorisation pour une action
     */
    @Override
    public boolean aAutorisationPour(String nomTable, String nomAction) {
        if (!estConnecte()) {
            LOGGER.log(Level.WARNING, "Tentative de vérification d'autorisation sans utilisateur connecté");
            return false;
        }

        try {
            // Vérification basique des permissions selon le rôle
            Integer roleUser = utilisateurConnecte.getRoleUtilisateur();
            
            // Exemple: tous les rôles >= 1 ont accès en lecture
            if ("read".equals(nomAction)) {
                return roleUser >= 1;
            }
            // Les rôles >= 2 peuvent créer/modifier/supprimer
            else if ("create".equals(nomAction) || "update".equals(nomAction) || "delete".equals(nomAction)) {
                return roleUser >= 2;
            }
            
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la vérification d'autorisation", e);
            return false;
        }
    }

    /**
     * Exige qu'un utilisateur soit connecté, sinon lance une exception
     */
    @Override
    public void exigerConnexion() throws CompteCourantException {
        if (!estConnecte()) {
            throw new CompteCourantException("Session expirée ou utilisateur non authentifié");
        }
    }

    /**
     * Exige une autorisation pour une action, sinon lance une exception
     */
    @Override
    public void exigerAutorisation(String nomTable, String nomAction) throws CompteCourantException {
        exigerConnexion();

        if (!aAutorisationPour(nomTable, nomAction)) {
            throw new CompteCourantException("Autorisation refusée pour l'action: " + nomTable + "." + nomAction);
        }
    }

    // ===== CRUD pour utilisateurs =====

    /**
     * Récupère tous les utilisateurs
     */
    @Override
    public List<Utilisateur> getAllUtilisateurs() throws CompteCourantException {
        exigerAutorisation("utilisateurs", "read");
        
        try {
            return em.createQuery("SELECT u FROM Utilisateur u", Utilisateur.class).getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de tous les utilisateurs", e);
            throw new CompteCourantException("Erreur lors de la récupération des utilisateurs: " + e.getMessage(), e);
        }
    }

    /**
     * Récupère un utilisateur par ID
     */
    @Override
    public Utilisateur getUtilisateurById(Integer id) throws CompteCourantException {
        exigerAutorisation("utilisateurs", "read");

        if (id == null || id <= 0) {
            throw new CompteCourantException("L'ID de l'utilisateur est obligatoire");
        }

        try {
            Utilisateur utilisateur = em.find(Utilisateur.class, id);
            if (utilisateur == null) {
                throw new CompteCourantException("Utilisateur introuvable avec ID: " + id);
            }
            return utilisateur;
        } catch (CompteCourantException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de l'utilisateur " + id, e);
            throw new CompteCourantException("Erreur lors de la récupération de l'utilisateur: " + e.getMessage(), e);
        }
    }

    /**
     * Crée un nouvel utilisateur
     */
    @Override
    public Utilisateur createUtilisateur(Utilisateur utilisateur) throws CompteCourantException {
        exigerAutorisation("utilisateurs", "create");

        if (utilisateur == null) {
            throw new CompteCourantException("Les données de l'utilisateur sont obligatoires");
        }

        validateUtilisateurData(utilisateur);

        try {
            // Vérifier que le nom d'utilisateur n'existe pas déjà
            try {
                em.createQuery(
                    "SELECT u FROM Utilisateur u WHERE u.nomUtilisateur = :nomUtilisateur",
                    Utilisateur.class
                ).setParameter("nomUtilisateur", utilisateur.getNomUtilisateur()).getSingleResult();
                
                throw new CompteCourantException("Un utilisateur avec ce nom existe déjà: " + utilisateur.getNomUtilisateur());
            } catch (NoResultException e) {
                // OK, le nom d'utilisateur est unique
            }

            em.persist(utilisateur);
            return utilisateur;
        } catch (CompteCourantException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création de l'utilisateur", e);
            throw new CompteCourantException("Erreur lors de la création de l'utilisateur: " + e.getMessage(), e);
        }
    }

    /**
     * Met à jour un utilisateur
     */
    @Override
    public Utilisateur updateUtilisateur(Utilisateur utilisateur) throws CompteCourantException {
        exigerAutorisation("utilisateurs", "update");

        if (utilisateur == null || utilisateur.getIdUtilisateur() == null) {
            throw new CompteCourantException("L'utilisateur et son ID sont obligatoires");
        }

        try {
            Utilisateur existant = em.find(Utilisateur.class, utilisateur.getIdUtilisateur());
            if (existant == null) {
                throw new CompteCourantException("Utilisateur introuvable avec ID: " + utilisateur.getIdUtilisateur());
            }

            validateUtilisateurData(utilisateur);

            return em.merge(utilisateur);
        } catch (CompteCourantException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour de l'utilisateur", e);
            throw new CompteCourantException("Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage(), e);
        }
    }

    /**
     * Supprime un utilisateur
     */
    @Override
    public void deleteUtilisateur(Integer id) throws CompteCourantException {
        exigerAutorisation("utilisateurs", "delete");

        if (id == null || id <= 0) {
            throw new CompteCourantException("L'ID de l'utilisateur est obligatoire");
        }

        try {
            Utilisateur utilisateur = em.find(Utilisateur.class, id);
            if (utilisateur == null) {
                throw new CompteCourantException("Utilisateur introuvable avec ID: " + id);
            }

            // Empêcher la suppression de l'utilisateur connecté
            if (estConnecte() && utilisateurConnecte.getIdUtilisateur().equals(id)) {
                throw new CompteCourantException("Impossible de supprimer l'utilisateur actuellement connecté");
            }

            em.remove(utilisateur);
        } catch (CompteCourantException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression de l'utilisateur " + id, e);
            throw new CompteCourantException("Erreur lors de la suppression de l'utilisateur: " + e.getMessage(), e);
        }
    }

    /**
     * Validation des données d'utilisateur
     */
    private void validateUtilisateurData(Utilisateur utilisateur) throws CompteCourantException {
        if (utilisateur.getNomUtilisateur() == null || utilisateur.getNomUtilisateur().trim().isEmpty()) {
            throw new CompteCourantException("Le nom d'utilisateur est obligatoire");
        }

        if (utilisateur.getMotDePasse() == null || utilisateur.getMotDePasse().trim().isEmpty()) {
            throw new CompteCourantException("Le mot de passe est obligatoire");
        }

        if (utilisateur.getRoleUtilisateur() == null || utilisateur.getRoleUtilisateur() < 0) {
            throw new CompteCourantException("Le rôle utilisateur est obligatoire et doit être positif");
        }
    }
}
