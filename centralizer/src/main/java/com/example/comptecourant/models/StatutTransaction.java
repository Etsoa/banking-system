package com.example.comptecourant.models;

public enum StatutTransaction {
    en_attente("En attente"),
    confirmee("Confirmee"),
    refusee("Refusee");

    private final String libelle;

    StatutTransaction(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
