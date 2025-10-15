package com.example.serveurcomptecourant.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CompteCourantAvecStatut {
    private Integer id;
    private LocalDateTime dateOuverture;
    private Boolean decouvert;
    private Integer idClient;
    private BigDecimal solde;
    private String statut;
    
    // Constructors
    public CompteCourantAvecStatut() {}
    
    public CompteCourantAvecStatut(CompteCourant compte, String statut) {
        this.id = compte.getId();
        this.dateOuverture = compte.getDateOuverture();
        this.decouvert = compte.getDecouvert();
        this.idClient = compte.getIdClient();
        this.solde = compte.getSolde();
        this.statut = statut;
    }
    
    // Getters & Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public LocalDateTime getDateOuverture() {
        return dateOuverture;
    }
    
    public void setDateOuverture(LocalDateTime dateOuverture) {
        this.dateOuverture = dateOuverture;
    }
    
    public Boolean getDecouvert() {
        return decouvert;
    }
    
    public void setDecouvert(Boolean decouvert) {
        this.decouvert = decouvert;
    }
    
    public Integer getIdClient() {
        return idClient;
    }
    
    public void setIdClient(Integer idClient) {
        this.idClient = idClient;
    }
    
    public BigDecimal getSolde() {
        return solde;
    }
    
    public void setSolde(BigDecimal solde) {
        this.solde = solde;
    }
    
    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
    }
}