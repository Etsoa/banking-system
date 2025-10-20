package com.example.centralizer.models.compteCourantDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Modèle représentant un transfert avec les frais associés
 */
public class TransfertAvecFrais {
    private String idTransfert;
    private String envoyer;
    private String receveur;
    private BigDecimal montant;
    private LocalDate dateTransfert;
    
    // Informations sur les frais
    private BigDecimal fraisEnvoyeur;
    private String libelleFraisEnvoyeur;
    private BigDecimal montantTotalEnvoyeur; // montant + frais
    
    // Constructeur par défaut
    public TransfertAvecFrais() {
    }

    // Getters et setters
    public String getIdTransfert() {
        return idTransfert;
    }

    public void setIdTransfert(String idTransfert) {
        this.idTransfert = idTransfert;
    }

    public String getEnvoyer() {
        return envoyer;
    }

    public void setEnvoyer(String envoyer) {
        this.envoyer = envoyer;
    }

    public String getReceveur() {
        return receveur;
    }

    public void setReceveur(String receveur) {
        this.receveur = receveur;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public LocalDate getDateTransfert() {
        return dateTransfert;
    }

    public void setDateTransfert(LocalDate dateTransfert) {
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