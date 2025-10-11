package com.example.centralizer.models.compteDepotDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Compte {
    private Integer idCompte;
    private LocalDateTime dateOuverture;
    private Integer idClient;
    private BigDecimal solde;

    // Constructors
    public Compte() {}

    public Compte(Integer idCompte, LocalDateTime dateOuverture, Integer idClient, BigDecimal solde) {
        this.idCompte = idCompte;
        this.dateOuverture = dateOuverture;
        this.idClient = idClient;
        this.solde = solde;
    }

    // Getters & Setters
    public Integer getIdCompte() { return idCompte; }
    public void setIdCompte(Integer idCompte) { this.idCompte = idCompte; }

    public LocalDateTime getDateOuverture() { return dateOuverture; }
    public void setDateOuverture(LocalDateTime dateOuverture) { this.dateOuverture = dateOuverture; }

    public Integer getIdClient() { return idClient; }
    public void setIdClient(Integer idClient) { this.idClient = idClient; }

    public BigDecimal getSolde() { return solde; }
    public void setSolde(BigDecimal solde) { this.solde = solde; }
}