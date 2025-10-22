package com.example.centralizer.dto;

import java.io.Serializable;

/**
 * DTO pour les requêtes de login
 */
public class LoginRequest implements Serializable {
    
    private String nomUtilisateur;
    private String motDePasse;
    private Integer idUtilisateur; // ID utilisateur optionnel (rempli après récupération du serveur)
    
    // Constructors
    public LoginRequest() {}
    
    public LoginRequest(String nomUtilisateur, String motDePasse) {
        this.nomUtilisateur = nomUtilisateur;
        this.motDePasse = motDePasse;
    }
    
    public LoginRequest(String nomUtilisateur, String motDePasse, Integer idUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
        this.motDePasse = motDePasse;
        this.idUtilisateur = idUtilisateur;
    }
    
    // Getters & Setters
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
    
    public Integer getIdUtilisateur() {
        return idUtilisateur;
    }
    
    public void setIdUtilisateur(Integer idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }
}
