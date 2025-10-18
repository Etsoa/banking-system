package com.example.serveurcomptecourant.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.json.bind.annotation.JsonbDateFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "frais")
public class Frais {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_frais")
    private Integer id;

    @Column(name = "date_debut", nullable = false)
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime dateDebut;

    @Column(name = "nom", nullable = false, length = 50)
    private String nom;

    @Column(name = "montant_min", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantMin;

    @Column(name = "montant_max", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantMax;

    @Column(name = "valeur", nullable = false)
    private Integer valeur;

    // Constructors
    public Frais() {}

    public Frais(LocalDateTime dateDebut, String nom, BigDecimal montantMin, 
                 BigDecimal montantMax, Integer valeur) {
        this.dateDebut = dateDebut;
        this.nom = nom;
        this.montantMin = montantMin;
        this.montantMax = montantMax;
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

    public BigDecimal getMontantMin() {
        return montantMin;
    }

    public void setMontantMin(BigDecimal montantMin) {
        this.montantMin = montantMin;
    }

    public BigDecimal getMontantMax() {
        return montantMax;
    }

    public void setMontantMax(BigDecimal montantMax) {
        this.montantMax = montantMax;
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
                ", montantMin=" + montantMin +
                ", montantMax=" + montantMax +
                ", valeur=" + valeur +
                '}';
    }
}