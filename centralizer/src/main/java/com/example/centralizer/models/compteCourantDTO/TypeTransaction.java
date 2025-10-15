package com.example.centralizer.models.compteCourantDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TypeTransaction {
    @JsonProperty("id")
    private Integer idTypeTransaction;
    private String libelle;
    private Boolean actif;
    private String signe;

    // Constructors
    public TypeTransaction() {}

    public TypeTransaction(Integer idTypeTransaction, String libelle, Boolean actif, String signe) {
        this.idTypeTransaction = idTypeTransaction;
        this.libelle = libelle;
        this.actif = actif;
        this.signe = signe;
    }

    // Getters & Setters
    public Integer getIdTypeTransaction() { return idTypeTransaction; }
    public void setIdTypeTransaction(Integer idTypeTransaction) { this.idTypeTransaction = idTypeTransaction; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }

    public String getSigne() { return signe; }
    public void setSigne(String signe) { this.signe = signe; }
}