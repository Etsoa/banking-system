package com.example.centralizer.dto.comptecourant;

/**
 * Enum TypeTransaction - Serializable
 */
public enum TypeTransaction {
    depot("Depot"),
    retrait("Retrait");

    private final String libelle;

    TypeTransaction(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
