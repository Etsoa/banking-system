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
@Table(name = "decouverte")
public class Decouverte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_frais")
    private Integer id;

    @Column(name = "date_debut", nullable = false)
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime dateDebut;

    @Column(name = "revenu_min", nullable = false, precision = 12, scale = 2)
    private BigDecimal revenuMin;

    @Column(name = "revenu_max", nullable = false, precision = 12, scale = 2)
    private BigDecimal revenuMax;

    @Column(name = "valeur", nullable = false)
    private Integer valeur;

    // Constructors
    public Decouverte() {}

    public Decouverte(LocalDateTime dateDebut, BigDecimal revenuMin, 
                      BigDecimal revenuMax, Integer valeur) {
        this.dateDebut = dateDebut;
        this.revenuMin = revenuMin;
        this.revenuMax = revenuMax;
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

    public BigDecimal getRevenuMin() {
        return revenuMin;
    }

    public void setRevenuMin(BigDecimal revenuMin) {
        this.revenuMin = revenuMin;
    }

    public BigDecimal getRevenuMax() {
        return revenuMax;
    }

    public void setRevenuMax(BigDecimal revenuMax) {
        this.revenuMax = revenuMax;
    }

    public Integer getValeur() {
        return valeur;
    }

    public void setValeur(Integer valeur) {
        this.valeur = valeur;
    }

    public BigDecimal getLimiteDecouverte() {
        return new BigDecimal(this.valeur);
    }

    @Override
    public String toString() {
        return "Decouverte{" +
                "id=" + id +
                ", dateDebut=" + dateDebut +
                ", revenuMin=" + revenuMin +
                ", revenuMax=" + revenuMax +
                ", valeur=" + valeur +
                '}';
    }
}