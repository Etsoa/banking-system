package com.example.centralizer.models.compteDepotDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Compte {
    private Integer idNum;
    private String idCompte;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime dateOuverture;
    
    private Integer idClient;
    private BigDecimal solde;

    // Constructors
    public Compte() {}

    public Compte(Integer idNum, String idCompte, LocalDateTime dateOuverture, Integer idClient, BigDecimal solde) {
        this.idNum = idNum;
        this.idCompte = idCompte;
        this.dateOuverture = dateOuverture;
        this.idClient = idClient;
        this.solde = solde;
    }

    // Getters & Setters
    public Integer getIdNum() { return idNum; }
    public void setIdNum(Integer idNum) { this.idNum = idNum; }

    public String getIdCompte() { return idCompte; }
    public void setIdCompte(String idCompte) { this.idCompte = idCompte; }

    public LocalDateTime getDateOuverture() { return dateOuverture; }
    public void setDateOuverture(LocalDateTime dateOuverture) { this.dateOuverture = dateOuverture; }

    public Integer getIdClient() { return idClient; }
    public void setIdClient(Integer idClient) { this.idClient = idClient; }

    public BigDecimal getSolde() { return solde; }
    public void setSolde(BigDecimal solde) { this.solde = solde; }
}