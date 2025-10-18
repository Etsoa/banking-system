package com.example.serveurpret.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.json.bind.annotation.JsonbDateFormat;
import java.time.LocalDateTime;

@Entity
@Table(name = "frais")
public class Frais {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_frais")
    private Integer id;

    @Column(name = "date_debut", nullable = false)
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateDebut;

    @Column(name = "nom", nullable = false, length = 50)
    private String nom;

    @Column(name = "valeur", nullable = false)
    private Integer valeur;

    // Constructors
    public Frais() {}

    public Frais(LocalDateTime dateDebut, String nom, Integer valeur) {
        this.dateDebut = dateDebut;
        this.nom = nom;
        this.valeur = valeur;
    }

    // Getters & Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Integer getValeur() {
        return valeur;
    }

    public void setValeur(Integer valeur) {
        this.valeur = valeur;
    }

    @Override
    public String toString() {
        return "Frais{" +
                "id=" + id +
                ", dateDebut=" + dateDebut +
                ", nom='" + nom + '\'' +
                ", valeur=" + valeur +
                '}';
    }
}