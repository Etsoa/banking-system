package com.example.centralizer.dto.comptecourant;

import java.io.Serializable;

/**
 * DTO pour Utilisateur (Serializable pour la sérialisation EJB)
 */
public class Utilisateur implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idUtilisateur;
    private String nomUtilisateur;
    private String motDePasse;
    private Integer roleUtilisateur;

    public Utilisateur() {}

    public Utilisateur(Integer idUtilisateur, String nomUtilisateur, String motDePasse, Integer roleUtilisateur) {
        this.idUtilisateur = idUtilisateur;
        this.nomUtilisateur = nomUtilisateur;
        this.motDePasse = motDePasse;
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

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public Integer getRoleUtilisateur() {
        return roleUtilisateur;
    }

    public void setRoleUtilisateur(Integer roleUtilisateur) {
        this.roleUtilisateur = roleUtilisateur;
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "idUtilisateur=" + idUtilisateur +
                ", nomUtilisateur='" + nomUtilisateur + '\'' +
                ", roleUtilisateur=" + roleUtilisateur +
                '}';
    }
}
