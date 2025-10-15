package com.example.centralizer.models.compteDepotDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CompteDepotAvecStatut {
    private Integer idCompte;
    private LocalDateTime dateOuverture;
    private Integer idClient;
    private BigDecimal solde;
    private String statutActuel;
    private LocalDateTime dateChangementStatut;
    private Boolean estActif;

    // Constructeurs
    public CompteDepotAvecStatut() {}

    // Getters et Setters
    public Integer getIdCompte() {
        return idCompte;
    }

    public void setIdCompte(Integer idCompte) {
        this.idCompte = idCompte;
    }

    public LocalDateTime getDateOuverture() {
        return dateOuverture;
    }

    public void setDateOuverture(LocalDateTime dateOuverture) {
        this.dateOuverture = dateOuverture;
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

    public String getStatutActuel() {
        return statutActuel;
    }

    public void setStatutActuel(String statutActuel) {
        this.statutActuel = statutActuel;
    }

    public LocalDateTime getDateChangementStatut() {
        return dateChangementStatut;
    }

    public void setDateChangementStatut(LocalDateTime dateChangementStatut) {
        this.dateChangementStatut = dateChangementStatut;
    }

    public Boolean getEstActif() {
        return estActif;
    }

    public void setEstActif(Boolean estActif) {
        this.estActif = estActif;
    }
}