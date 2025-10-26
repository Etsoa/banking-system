package com.example.centralizer.dto.comptecourant;

/**
 * Enum TypeTransaction - Serializable
 */
public enum TypeTransaction {
    DEPOT("Depot"),
    RETRAIT("Retrait");

    private final String libelle;

    TypeTransaction(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
