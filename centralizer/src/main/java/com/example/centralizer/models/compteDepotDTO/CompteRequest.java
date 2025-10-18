package com.example.centralizer.models.compteDepotDTO;

/**
 * DTO pour les requêtes avec un ID de compte
 */
public class CompteRequest {
    private String compteId;

    // Constructeurs
    public CompteRequest() {}

    public CompteRequest(String compteId) {
        this.compteId = compteId;
    }

    // Getters et Setters
    public String getCompteId() {
        return compteId;
    }

    public void setCompteId(String compteId) {
        this.compteId = compteId;
    }
}