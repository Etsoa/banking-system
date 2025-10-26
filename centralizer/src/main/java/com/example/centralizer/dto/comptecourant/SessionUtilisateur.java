package com.example.centralizer.dto.comptecourant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO représentant la session utilisateur complète
 * Stocke l'utilisateur connecté avec tous ses rôles et actions/permissions
 */
public class SessionUtilisateur implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idUtilisateur;
    private String nomUtilisateur;
    private Integer roleUtilisateur;  // ID du rôle
    private List<ActionRoleDTO> actionsRoles;  // Liste complète des actions/permissions

    public SessionUtilisateur() {
        this.actionsRoles = new ArrayList<>();
    }

    public SessionUtilisateur(Integer idUtilisateur, String nomUtilisateur, Integer roleUtilisateur) {
        this();
        this.idUtilisateur = idUtilisateur;
        this.nomUtilisateur = nomUtilisateur;
        this.roleUtilisateur = roleUtilisateur;
    }

    public Integer getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Integer idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }

    public Integer getRoleUtilisateur() {
        return roleUtilisateur;
    }

    public void setRoleUtilisateur(Integer roleUtilisateur) {
        this.roleUtilisateur = roleUtilisateur;
    }

    public List<ActionRoleDTO> getActionsRoles() {
        return actionsRoles;
    }

    public void setActionsRoles(List<ActionRoleDTO> actionsRoles) {
        this.actionsRoles = actionsRoles;
    }

    public void addActionRole(ActionRoleDTO actionRole) {
        if (this.actionsRoles == null) {
            this.actionsRoles = new ArrayList<>();
        }
        this.actionsRoles.add(actionRole);
    }

    /**
     * Vérifie si l'utilisateur a une permission spécifique
     */
    public boolean aAutorisationPour(String nomTable, String nomAction) {
        if (actionsRoles == null) {
            return false;
        }
        return actionsRoles.stream()
            .anyMatch(ar -> ar.getNomTable().equals(nomTable) && ar.getNomAction().equals(nomAction));
    }

    /**
     * Récupère toutes les actions pour une table
     */
    public List<ActionRoleDTO> getActionsForTable(String nomTable) {
        if (actionsRoles == null) {
            return new ArrayList<>();
        }
        return actionsRoles.stream()
            .filter(ar -> ar.getNomTable().equals(nomTable))
            .toList();
    }

    @Override
    public String toString() {
        return "SessionUtilisateur{" +
                "idUtilisateur=" + idUtilisateur +
                ", nomUtilisateur='" + nomUtilisateur + '\'' +
                ", roleUtilisateur=" + roleUtilisateur +
                ", actionsRoles=" + (actionsRoles != null ? actionsRoles.size() : 0) + " permissions" +
                '}';
    }
}
