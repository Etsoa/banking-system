package com.example.serveurcomptecourant.services;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.serveurcomptecourant.exceptions.SecurityException;
import com.example.serveurcomptecourant.models.ActionRole;
import com.example.serveurcomptecourant.models.Direction;
import com.example.serveurcomptecourant.models.Utilisateur;
import com.example.serveurcomptecourant.repository.ActionRoleRepository;
import com.example.serveurcomptecourant.repository.UtilisateurRepository;

import jakarta.ejb.EJB;
import jakarta.ejb.Remove;
import jakarta.ejb.Stateful;

@Stateful
public class UtilisateurService {
    private static final Logger LOGGER = Logger.getLogger(UtilisateurService.class.getName());
    
    @EJB
    private UtilisateurRepository utilisateurRepository;
    
    @EJB
    private ActionRoleRepository actionRoleRepository;
    
    // Session state - utilisateur connecté
    private Utilisateur utilisateurConnecte;
    private boolean estConnecte = false;
    private List<ActionRole> rolesUtilisateur; // Rôles en session

    /**
     * Authentifie un utilisateur et démarre une session
     */
    public boolean login(String nomUtilisateur, String motDePasse) {
        try {
            if (nomUtilisateur == null || nomUtilisateur.trim().isEmpty()) {
                throw new IllegalArgumentException("Le nom d'utilisateur est obligatoire");
            }
            if (motDePasse == null || motDePasse.trim().isEmpty()) {
                throw new IllegalArgumentException("Le mot de passe est obligatoire");
            }
            
            // Rechercher l'utilisateur par nom d'utilisateur
            Utilisateur utilisateur = utilisateurRepository.findByNomUtilisateur(nomUtilisateur);
            
            if (utilisateur == null) {
                LOGGER.log(Level.WARNING, "Tentative de connexion avec un nom d''utilisateur inexistant: {0}", nomUtilisateur);
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
            
            // Charger les rôles/autorisations de l'utilisateur en session
            this.rolesUtilisateur = chargerRolesUtilisateur(utilisateur.getRoleUtilisateur());
            
            LOGGER.log(Level.INFO, "Utilisateur connecté: {0} (Role: {1}) - {2} autorisations chargées", 
                new Object[]{nomUtilisateur, utilisateur.getRoleUtilisateur(), rolesUtilisateur.size()});
            return true;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l''authentification de l''utilisateur {0}", new Object[]{nomUtilisateur, e});
            return false;
        }
    }

    /**
     * Déconnecte l'utilisateur et termine la session
     */
    @Remove
    public void logout() {
        if (utilisateurConnecte != null) {
            LOGGER.log(Level.INFO, "Déconnexion de l''utilisateur: {0}", utilisateurConnecte.getNomUtilisateur());
        }
        this.utilisateurConnecte = null;
        this.estConnecte = false;
        this.rolesUtilisateur = null; // Nettoyer les rôles
    }

    /**
     * Vérifie si un utilisateur est connecté
     */
    public boolean estConnecte() {
        return estConnecte && utilisateurConnecte != null;
    }

    /**
     * Récupère l'utilisateur connecté
     */
    public Utilisateur getUtilisateurConnecte() {
        if (!estConnecte()) {
            throw new IllegalStateException("Aucun utilisateur connecté");
        }
        return utilisateurConnecte;
    }

    /**
     * Récupère le rôle de l'utilisateur connecté
     */
    public Integer getRoleUtilisateurConnecte() {
        if (!estConnecte()) {
            throw new IllegalStateException("Aucun utilisateur connecté");
        }
        return utilisateurConnecte.getRoleUtilisateur();
    }

    /**
     * Récupère la direction de l'utilisateur connecté
     */
    public Direction getDirectionUtilisateurConnecte() {
        if (!estConnecte()) {
            throw new IllegalStateException("Aucun utilisateur connecté");
        }
        return utilisateurConnecte.getDirection();
    }

    /**
     * Récupère les rôles/autorisations de l'utilisateur connecté
     */
    public List<ActionRole> getRolesUtilisateurConnecte() {
        if (!estConnecte()) {
            throw new IllegalStateException("Aucun utilisateur connecté");
        }
        return rolesUtilisateur != null ? new ArrayList<>(rolesUtilisateur) : new ArrayList<>();
    }

    /**
     * Charge les rôles/autorisations d'un utilisateur selon son niveau de rôle
     */
    private List<ActionRole> chargerRolesUtilisateur(Integer roleUtilisateur) {
        try {
            // Récupérer toutes les actions que l'utilisateur peut faire selon son rôle
            List<ActionRole> toutesLesActions = actionRoleRepository.findAll();
            
            return toutesLesActions.stream()
                .filter(action -> roleUtilisateur >= action.getRoleMinimum())
                .toList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement des rôles pour le niveau {0}", new Object[]{roleUtilisateur, e});
            return new ArrayList<>();
        }
    }

    /**
     * Vérifie si l'utilisateur connecté a l'autorisation pour une action sur une table
     * Utilise les rôles mis en session lors du login
     */
    public boolean aAutorisationPour(String nomTable, String nomAction) {
        if (!estConnecte()) {
            LOGGER.log(Level.WARNING, "Tentative de vérification d'autorisation sans utilisateur connecté");
            return false;
        }
        
        if (rolesUtilisateur == null || rolesUtilisateur.isEmpty()) {
            LOGGER.log(Level.WARNING, "Aucun rôle en session pour l''utilisateur {0}", utilisateurConnecte.getNomUtilisateur());
            return false;
        }
        
        try {
            // Rechercher l'action dans les rôles mis en session
            boolean autorise = rolesUtilisateur.stream()
                .anyMatch(role -> nomTable.equals(role.getNomTable()) && nomAction.equals(role.getNomAction()));
            
            if (!autorise) {
                LOGGER.log(Level.WARNING, "Autorisation refusée pour {0} (role {1}) pour action {2}.{3}", 
                    new Object[]{utilisateurConnecte.getNomUtilisateur(), utilisateurConnecte.getRoleUtilisateur(), nomTable, nomAction});
            } else {
                LOGGER.log(Level.FINE, "Autorisation accordée pour {0} pour action {1}.{2}", 
                    new Object[]{utilisateurConnecte.getNomUtilisateur(), nomTable, nomAction});
            }
            
            return autorise;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la vérification d''autorisation pour {0}.{1}", new Object[]{nomTable, nomAction, e});
            return false;
        }
    }

    /**
     * Exige qu'un utilisateur soit connecté, sinon lance une exception
     */
    public void exigerConnexion() throws SecurityException {
        if (!estConnecte()) {
            throw new SecurityException.SessionExpiredException();
        }
    }

    /**
     * Exige une autorisation pour une action, sinon lance une exception
     */
    public void exigerAutorisation(String nomTable, String nomAction) throws SecurityException {
        exigerConnexion();
        
        if (!aAutorisationPour(nomTable, nomAction)) {
            throw new SecurityException.AutorisationException(
                utilisateurConnecte.getNomUtilisateur(), nomTable, nomAction);
        }
    }

    // ===== Méthodes CRUD pour la gestion des utilisateurs =====

    /**
     * Récupère tous les utilisateurs (nécessite autorisation)
     */
    public List<Utilisateur> getAllUtilisateurs() throws SecurityException {
        exigerAutorisation("utilisateurs", "read");
        
        try {
            return utilisateurRepository.findAll();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de tous les utilisateurs", e);
            throw new RuntimeException("Erreur lors de la récupération des utilisateurs", e);
        }
    }

    /**
     * Récupère un utilisateur par ID (nécessite autorisation)
     */
    public Utilisateur getUtilisateurById(Integer id) throws SecurityException {
        exigerAutorisation("utilisateurs", "read");
        
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID de l'utilisateur est obligatoire");
        }
        
        try {
            Utilisateur utilisateur = utilisateurRepository.find(id);
            if (utilisateur == null) {
                throw new IllegalArgumentException("Utilisateur introuvable avec ID: " + id);
            }
            return utilisateur;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de l''utilisateur {0}", new Object[]{id, e});
            throw new RuntimeException("Erreur lors de la récupération de l'utilisateur", e);
        }
    }

    /**
     * Crée un nouvel utilisateur (nécessite autorisation)
     */
    public Utilisateur createUtilisateur(Utilisateur utilisateur) throws SecurityException {
        exigerAutorisation("utilisateurs", "create");
        
        if (utilisateur == null) {
            throw new IllegalArgumentException("Les données de l'utilisateur sont obligatoires");
        }
        
        validateUtilisateurData(utilisateur);
        
        try {
            // Vérifier que le nom d'utilisateur n'existe pas déjà
            Utilisateur existant = utilisateurRepository.findByNomUtilisateur(utilisateur.getNomUtilisateur());
            if (existant != null) {
                throw new IllegalArgumentException("Un utilisateur avec ce nom existe déjà: " + utilisateur.getNomUtilisateur());
            }
            
            return utilisateurRepository.save(utilisateur);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création de l''utilisateur", e);
            throw new RuntimeException("Erreur lors de la création de l'utilisateur", e);
        }
    }

    /**
     * Met à jour un utilisateur (nécessite autorisation)
     */
    public Utilisateur updateUtilisateur(Utilisateur utilisateur) throws SecurityException {
        exigerAutorisation("utilisateurs", "update");
        
        if (utilisateur == null || utilisateur.getIdUtilisateur() == null) {
            throw new IllegalArgumentException("L'utilisateur et son ID sont obligatoires");
        }
        
        try {
            // Vérifier que l'utilisateur existe
            Utilisateur existant = utilisateurRepository.find(utilisateur.getIdUtilisateur());
            if (existant == null) {
                throw new IllegalArgumentException("Utilisateur introuvable avec ID: " + utilisateur.getIdUtilisateur());
            }
            
            validateUtilisateurData(utilisateur);
            
            // Vérifier l'unicité du nom d'utilisateur (sauf pour lui-même)
            Utilisateur avecMemeNom = utilisateurRepository.findByNomUtilisateur(utilisateur.getNomUtilisateur());
            if (avecMemeNom != null && !avecMemeNom.getIdUtilisateur().equals(utilisateur.getIdUtilisateur())) {
                throw new IllegalArgumentException("Un autre utilisateur avec ce nom existe déjà: " + utilisateur.getNomUtilisateur());
            }
            
            return utilisateurRepository.save(utilisateur);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour de l''utilisateur {0}", new Object[]{utilisateur.getIdUtilisateur(), e});
            throw new RuntimeException("Erreur lors de la mise à jour de l'utilisateur", e);
        }
    }

    /**
     * Supprime un utilisateur (nécessite autorisation)
     */
    public void deleteUtilisateur(Integer id) throws SecurityException {
        exigerAutorisation("utilisateurs", "delete");
        
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID de l'utilisateur est obligatoire");
        }
        
        try {
            Utilisateur utilisateur = utilisateurRepository.find(id);
            if (utilisateur == null) {
                throw new IllegalArgumentException("Utilisateur introuvable avec ID: " + id);
            }
            
            // Empêcher la suppression de l'utilisateur connecté
            if (estConnecte() && utilisateurConnecte.getIdUtilisateur().equals(id)) {
                throw new IllegalArgumentException("Impossible de supprimer l'utilisateur actuellement connecté");
            }
            
            utilisateurRepository.delete(utilisateur);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression de l''utilisateur {0}", new Object[]{id, e});
            throw new RuntimeException("Erreur lors de la suppression de l'utilisateur", e);
        }
    }

    /**
     * Validation des données d'utilisateur
     */
    private void validateUtilisateurData(Utilisateur utilisateur) {
        if (utilisateur.getNomUtilisateur() == null || utilisateur.getNomUtilisateur().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom d'utilisateur est obligatoire");
        }
        
        if (utilisateur.getMotDePasse() == null || utilisateur.getMotDePasse().trim().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire");
        }
        
        if (utilisateur.getRoleUtilisateur() == null || utilisateur.getRoleUtilisateur() < 0) {
            throw new IllegalArgumentException("Le rôle utilisateur est obligatoire et doit être positif");
        }
    }
}