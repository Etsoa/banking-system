package com.example.centralizer.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO pour CompteCourant (reçu du ServeurCompteCourant)
 */
public class CompteCourant implements Serializable {
    
    private Integer idCompte;
    private BigDecimal solde;
    
    // Constructors
    public CompteCourant() {
        this.solde = BigDecimal.ZERO;
    }
    
    public CompteCourant(BigDecimal solde) {
        this.solde = solde;
    }
    
    // Getters & Setters
    public Integer getIdCompte() {
        return idCompte;
    }
    
    public void setIdCompte(Integer idCompte) {
        this.idCompte = idCompte;
    }
    
    public BigDecimal getSolde() {
        return solde;
    }
    
    public void setSolde(BigDecimal solde) {
        this.solde = solde;
    }
    
    @Override
    public String toString() {
        return "CompteCourant{idCompte=" + idCompte + ", solde=" + solde + "}";
    }
}
