package com.example.centralizer.models.compteCourantDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Decouverte {
    private Integer id;
    private LocalDateTime dateDebut;
    private BigDecimal revenuMin;
    private BigDecimal revenuMax;
    private Integer valeur;

    // Constructors
    public Decouverte() {}

    public Decouverte(Integer id, LocalDateTime dateDebut, BigDecimal revenuMin, 
                      BigDecimal revenuMax, Integer valeur) {
        this.id = id;
        this.dateDebut = dateDebut;
        this.revenuMin = revenuMin;
        this.revenuMax = revenuMax;
        this.valeur = valeur;
    }

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }

    public BigDecimal getRevenuMin() { return revenuMin; }
    public void setRevenuMin(BigDecimal revenuMin) { this.revenuMin = revenuMin; }

    public BigDecimal getRevenuMax() { return revenuMax; }
    public void setRevenuMax(BigDecimal revenuMax) { this.revenuMax = revenuMax; }

    public Integer getValeur() { return valeur; }
    public void setValeur(Integer valeur) { this.valeur = valeur; }

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