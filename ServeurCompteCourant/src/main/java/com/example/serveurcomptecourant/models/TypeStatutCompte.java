package com.example.serveurcomptecourant.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "types_statut_compte")
public class TypeStatutCompte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type_statut_compte")
    private Integer id;

    @Column(name = "libelle", nullable = false, length = 50)
    private String libelle;

    @Column(name = "actif", nullable = false)
    private Boolean actif = true;

    // Constructors
    public TypeStatutCompte() {}

    public TypeStatutCompte(String libelle) {
        this.libelle = libelle;
        this.actif = true;
    }

    public TypeStatutCompte(String libelle, Boolean actif) {
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

    @Override
    public String toString() {
        return "TypeStatutCompte{" +
                "id=" + id +
                ", libelle='" + libelle + '\'' +
                ", actif=" + actif +
                '}';
    }
}