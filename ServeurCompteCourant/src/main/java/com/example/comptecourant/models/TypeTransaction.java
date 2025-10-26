package com.example.comptecourant.models;

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
