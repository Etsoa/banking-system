package com.example.centralizer.dto;

import java.io.Serializable;

/**
 * DTO pour les réponses de login
 */
public class LoginResponse implements Serializable {
    
    private Integer idUtilisateur;
    private String nomUtilisateur;
    private Integer idDirection;
    private Integer roleUtilisateur;
    private String message;
    private boolean success;
    
    // Constructors
    public LoginResponse() {}
    
    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    // Getters & Setters
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
    
    public Integer getIdDirection() {
        return idDirection;
    }
    
    public void setIdDirection(Integer idDirection) {
        this.idDirection = idDirection;
    }
    
    public Integer getRoleUtilisateur() {
        return roleUtilisateur;
    }
    
    public void setRoleUtilisateur(Integer roleUtilisateur) {
        this.roleUtilisateur = roleUtilisateur;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
}
