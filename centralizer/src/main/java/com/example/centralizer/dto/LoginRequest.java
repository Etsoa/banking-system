package com.example.centralizer.dto;

import java.io.Serializable;

/**
 * DTO pour les requêtes de login
 */
public class LoginRequest implements Serializable {
    
    private String nomUtilisateur;
    private String motDePasse;
    
    // Constructors
    public LoginRequest() {}
    
    public LoginRequest(String nomUtilisateur, String motDePasse) {
        this.nomUtilisateur = nomUtilisateur;
        this.motDePasse = motDePasse;
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
}
