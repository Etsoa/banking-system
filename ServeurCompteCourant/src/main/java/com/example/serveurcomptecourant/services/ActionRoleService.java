package com.example.serveurcomptecourant.services;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.serveurcomptecourant.models.ActionRole;
import com.example.serveurcomptecourant.repository.ActionRoleRepository;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class ActionRoleService {
    private static final Logger LOGGER = Logger.getLogger(ActionRoleService.class.getName());
    
    @EJB
    private ActionRoleRepository actionRoleRepository;

    /**
     * Récupère toutes les actions/rôles
     */
    public List<ActionRole> getAllActionRoles() {
        try {
            return actionRoleRepository.findAll();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de toutes les actions/rôles", e);
            throw new RuntimeException("Erreur lors de la récupération des actions/rôles", e);
        }
    }

    /**
     * Récupère une action/rôle par ID
     */
    public ActionRole getActionRoleById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID de l'action/rôle est obligatoire");
        }
        
        try {
            ActionRole actionRole = actionRoleRepository.find(id);
            if (actionRole == null) {
                throw new IllegalArgumentException("Action/rôle introuvable avec ID: " + id);
            }
            return actionRole;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de l'action/rôle " + id, e);
            throw new RuntimeException("Erreur lors de la récupération de l'action/rôle", e);
        }
    }

    /**
     * Récupère les actions/rôles par table
     */
    public List<ActionRole> getActionRolesByTable(String nomTable) {
        if (nomTable == null || nomTable.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la table est obligatoire");
        }
        
        try {
            return actionRoleRepository.findByTable(nomTable);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des actions/rôles pour la table " + nomTable, e);
            throw new RuntimeException("Erreur lors de la récupération des actions/rôles", e);
        }
    }

    /**
     * Récupère le rôle minimum requis pour une action
     */
    public Integer getRoleMinimumPourAction(String nomTable, String nomAction) {
        if (nomTable == null || nomTable.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la table est obligatoire");
        }
        if (nomAction == null || nomAction.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'action est obligatoire");
        }
        
        try {
            return actionRoleRepository.getRoleMinimumForAction(nomTable, nomAction);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération du rôle minimum pour " + nomTable + "." + nomAction, e);
            throw new RuntimeException("Erreur lors de la récupération du rôle minimum", e);
        }
    }

    /**
     * Crée une nouvelle action/rôle
     */
    public ActionRole createActionRole(ActionRole actionRole) {
        if (actionRole == null) {
            throw new IllegalArgumentException("Les données de l'action/rôle sont obligatoires");
        }
        
        validateActionRoleData(actionRole);
        
        try {
            return actionRoleRepository.save(actionRole);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création de l'action/rôle", e);
            throw new RuntimeException("Erreur lors de la création de l'action/rôle", e);
        }
    }

    /**
     * Met à jour une action/rôle
     */
    public ActionRole updateActionRole(ActionRole actionRole) {
        if (actionRole == null || actionRole.getIdActionRole() == null) {
            throw new IllegalArgumentException("L'action/rôle et son ID sont obligatoires");
        }
        
        try {
            // Vérifier que l'action/rôle existe
            ActionRole existante = actionRoleRepository.find(actionRole.getIdActionRole());
            if (existante == null) {
                throw new IllegalArgumentException("Action/rôle introuvable avec ID: " + actionRole.getIdActionRole());
            }
            
            validateActionRoleData(actionRole);
            
            return actionRoleRepository.save(actionRole);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour de l'action/rôle " + actionRole.getIdActionRole(), e);
            throw new RuntimeException("Erreur lors de la mise à jour de l'action/rôle", e);
        }
    }

    /**
     * Supprime une action/rôle
     */
    public void deleteActionRole(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID de l'action/rôle est obligatoire");
        }
        
        try {
            ActionRole actionRole = actionRoleRepository.find(id);
            if (actionRole == null) {
                throw new IllegalArgumentException("Action/rôle introuvable avec ID: " + id);
            }
            
            actionRoleRepository.delete(actionRole);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression de l'action/rôle " + id, e);
            throw new RuntimeException("Erreur lors de la suppression de l'action/rôle", e);
        }
    }

    /**
     * Validation des données d'action/rôle
     */
    private void validateActionRoleData(ActionRole actionRole) {
        if (actionRole.getNomTable() == null || actionRole.getNomTable().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la table est obligatoire");
        }
        
        if (actionRole.getNomAction() == null || actionRole.getNomAction().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'action est obligatoire");
        }
        
        if (actionRole.getRoleMinimum() == null || actionRole.getRoleMinimum() < 0) {
            throw new IllegalArgumentException("Le rôle minimum est obligatoire et doit être positif");
        }
    }
}