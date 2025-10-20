package com.example.centralizer.dto;

import java.io.Serializable;

/**
 * DTO pour Direction
 */
public class Direction implements Serializable {
    
    private Integer idDirection;
    private String nomDirection;
    private String description;
    
    // Constructors
    public Direction() {}
    
    public Direction(Integer idDirection, String nomDirection) {
        this.idDirection = idDirection;
        this.nomDirection = nomDirection;
    }
    
    // Getters & Setters
    public Integer getIdDirection() {
        return idDirection;
    }
    
    public void setIdDirection(Integer idDirection) {
        this.idDirection = idDirection;
    }
    
    public String getNomDirection() {
        return nomDirection;
    }
    
    public void setNomDirection(String nomDirection) {
        this.nomDirection = nomDirection;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
