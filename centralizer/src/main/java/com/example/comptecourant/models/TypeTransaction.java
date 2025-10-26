package com.example.comptecourant.models;

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
