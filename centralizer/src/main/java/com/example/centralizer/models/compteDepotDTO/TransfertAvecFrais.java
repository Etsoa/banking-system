package com.example.centralizer.models.compteDepotDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Modèle représentant un transfert avec les frais associés pour les comptes dépôt
 */
public class TransfertAvecFrais {
    @JsonProperty("idTransfert")
    private int idTransfert;
    
    @JsonProperty("compteEnvoyeur")
    private String compteEnvoyeur;
    
    @JsonProperty("compteReceveur")
    private String compteReceveur;
    
    @JsonProperty("montant")
    private BigDecimal montant;
    @JsonProperty("dateTransfert")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime dateTransfert;
    
    // Informations sur les frais
    @JsonProperty("fraisEnvoyeur")
    private BigDecimal fraisEnvoyeur;
    
    @JsonProperty("libelleFraisEnvoyeur")
    private String libelleFraisEnvoyeur;
    
    @JsonProperty("montantTotalEnvoyeur")
    private BigDecimal montantTotalEnvoyeur; // montant + frais
    
    // Constructeur par défaut
    public TransfertAvecFrais() {
    }

    // Getters et setters
    public int getIdTransfert() {
        return idTransfert;
    }

    public void setIdTransfert(int idTransfert) {
        this.idTransfert = idTransfert;
    }

    public String getCompteEnvoyeur() {
        return compteEnvoyeur;
    }

    public void setCompteEnvoyeur(String compteEnvoyeur) {
        this.compteEnvoyeur = compteEnvoyeur;
    }

    public String getCompteReceveur() {
        return compteReceveur;
    }

    public void setCompteReceveur(String compteReceveur) {
        this.compteReceveur = compteReceveur;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public LocalDateTime getDateTransfert() {
        return dateTransfert;
    }

    public void setDateTransfert(LocalDateTime dateTransfert) {
        this.dateTransfert = dateTransfert;
    }

    public BigDecimal getFraisEnvoyeur() {
        return fraisEnvoyeur;
    }

    public void setFraisEnvoyeur(BigDecimal fraisEnvoyeur) {
        this.fraisEnvoyeur = fraisEnvoyeur;
    }

    public String getLibelleFraisEnvoyeur() {
        return libelleFraisEnvoyeur;
    }

    public void setLibelleFraisEnvoyeur(String libelleFraisEnvoyeur) {
        this.libelleFraisEnvoyeur = libelleFraisEnvoyeur;
    }

    public BigDecimal getMontantTotalEnvoyeur() {
        return montantTotalEnvoyeur;
    }

    public void setMontantTotalEnvoyeur(BigDecimal montantTotalEnvoyeur) {
        this.montantTotalEnvoyeur = montantTotalEnvoyeur;
    }
}