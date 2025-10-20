package com.example.centralizer.dto;

import java.io.Serializable;

/**
 * DTO pour ActionRole
 */
public class ActionRole implements Serializable {
    
    private Integer idAction;
    private String nomAction;
    private String description;
    
    // Constructors
    public ActionRole() {}
    
    public ActionRole(Integer idAction, String nomAction) {
        this.idAction = idAction;
        this.nomAction = nomAction;
    }
    
    // Getters & Setters
    public Integer getIdAction() {
        return idAction;
    }
    
    public void setIdAction(Integer idAction) {
        this.idAction = idAction;
    }
    
    public String getNomAction() {
        return nomAction;
    }
    
    public void setNomAction(String nomAction) {
        this.nomAction = nomAction;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
