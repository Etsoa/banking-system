package com.example.serveurpret.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "plage_duree_pret")
public class PlageDureePret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plage")
    private Integer id;

    @Column(name = "montant_min", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantMin;

    @Column(name = "montant_max", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantMax;

    @Column(name = "duree_min_mois", nullable = false)
    private Integer dureeMinMois;

    @Column(name = "duree_max_mois", nullable = false)
    private Integer dureeMaxMois;

    @Column(name = "actif", nullable = false)
    private Boolean actif = true;

    // Constructors
    public PlageDureePret() {}

    public PlageDureePret(BigDecimal montantMin, BigDecimal montantMax, 
                         Integer dureeMinMois, Integer dureeMaxMois) {
        this.montantMin = montantMin;
        this.montantMax = montantMax;
        this.dureeMinMois = dureeMinMois;
        this.dureeMaxMois = dureeMaxMois;
        this.actif = true;
    }

    public PlageDureePret(BigDecimal montantMin, BigDecimal montantMax, 
                         Integer dureeMinMois, Integer dureeMaxMois, Boolean actif) {
        this.montantMin = montantMin;
        this.montantMax = montantMax;
        this.dureeMinMois = dureeMinMois;
        this.dureeMaxMois = dureeMaxMois;
        this.actif = actif;
    }

    // Getters & Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getDureeMinMois() {
        return dureeMinMois;
    }

    public void setDureeMinMois(Integer dureeMinMois) {
        this.dureeMinMois = dureeMinMois;
    }

    public Integer getDureeMaxMois() {
        return dureeMaxMois;
    }

    public void setDureeMaxMois(Integer dureeMaxMois) {
        this.dureeMaxMois = dureeMaxMois;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    @Override
    public String toString() {
        return "PlageDureePret{" +
                "id=" + id +
                ", montantMin=" + montantMin +
                ", montantMax=" + montantMax +
                ", dureeMinMois=" + dureeMinMois +
                ", dureeMaxMois=" + dureeMaxMois +
                ", actif=" + actif +
                '}';
    }
}