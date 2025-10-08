package com.example.serveurpret.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "modalites")
public class Modalite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_modalite")
    private Integer id;

    @Column(name = "libelle", nullable = false, length = 50)
    private String libelle;

    @Column(name = "nombre_mois", nullable = false, length = 50)
    private String nombreMois;

    @Column(name = "actif", nullable = false)
    private Boolean actif = true;

    // Constructors
    public Modalite() {}

    public Modalite(String libelle, String nombreMois) {
        this.libelle = libelle;
        this.nombreMois = nombreMois;
        this.actif = true;
    }

    public Modalite(String libelle, String nombreMois, Boolean actif) {
        this.libelle = libelle;
        this.nombreMois = nombreMois;
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

    public String getNombreMois() {
        return nombreMois;
    }

    public void setNombreMois(String nombreMois) {
        this.nombreMois = nombreMois;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }
}