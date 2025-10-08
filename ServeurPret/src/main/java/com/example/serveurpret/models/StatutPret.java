package com.example.serveurpret.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "statuts_pret")
public class StatutPret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_statut_pret")
    private Integer id;

    @Column(name = "libelle", nullable = false, length = 50)
    private String libelle;

    @Column(name = "actif", nullable = false)
    private Boolean actif = true;

    // Constructors
    public StatutPret() {}

    public StatutPret(String libelle) {
        this.libelle = libelle;
        this.actif = true;
    }

    public StatutPret(String libelle, Boolean actif) {
        this.libelle = libelle;
        this.actif = actif;
    }

    // Getters & Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }
}