package com.example.comptecourant.models;

public enum StatutTransaction {
    EN_ATTENTE("En attente"),
    CONFIRMEE("Confirmee"),
    REFUSEE("Refusee");

    private final String libelle;

    StatutTransaction(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
