package com.example.centralizer.models.compteDepotDTO;

/**
 * DTO pour les requêtes de transfert
 */
public class TransfertRequest {
    private String compteEnvoyeur;
    private String compteReceveur;
    private java.math.BigDecimal montant;

    // Constructeurs
    public TransfertRequest() {}

    public TransfertRequest(String compteEnvoyeur, String compteReceveur, java.math.BigDecimal montant) {
        this.compteEnvoyeur = compteEnvoyeur;
        this.compteReceveur = compteReceveur;
        this.montant = montant;
    }

    // Getters et Setters
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

    public java.math.BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(java.math.BigDecimal montant) {
        this.montant = montant;
    }
}