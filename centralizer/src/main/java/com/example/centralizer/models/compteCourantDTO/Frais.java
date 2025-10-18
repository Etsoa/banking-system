package com.example.centralizer.models.compteCourantDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Frais {
    private Integer id;
    private LocalDateTime dateDebut;
    private String nom;
    private BigDecimal montantMin;
    private BigDecimal montantMax;
    private Integer valeur;

    // Constructors
    public Frais() {}

    public Frais(Integer id, LocalDateTime dateDebut, String nom, 
                 BigDecimal montantMin, BigDecimal montantMax, Integer valeur) {
        this.id = id;
        this.dateDebut = dateDebut;
        this.nom = nom;
        this.montantMin = montantMin;
        this.montantMax = montantMax;
        this.valeur = valeur;
    }

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public BigDecimal getMontantMin() { return montantMin; }
    public void setMontantMin(BigDecimal montantMin) { this.montantMin = montantMin; }

    public BigDecimal getMontantMax() { return montantMax; }
    public void setMontantMax(BigDecimal montantMax) { this.montantMax = montantMax; }

    public Integer getValeur() { return valeur; }
    public void setValeur(Integer valeur) { this.valeur = valeur; }

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