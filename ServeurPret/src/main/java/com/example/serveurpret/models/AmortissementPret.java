package com.example.serveurpret.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.json.bind.annotation.JsonbDateFormat;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "amortissement_pret")
public class AmortissementPret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_amortissement")
    private Integer id;

    @Column(name = "id_pret", nullable = false)
    private Integer idPret;

    @Column(name = "periode", nullable = false)
    private Integer periode;

    @Column(name = "date_echeance", nullable = false)
    @JsonbDateFormat("yyyy-MM-dd")
    private LocalDate dateEcheance;

    @Column(name = "capital_debut", nullable = false, precision = 12, scale = 2)
    private BigDecimal capitalDebut;

    @Column(name = "interets", nullable = false, precision = 12, scale = 2)
    private BigDecimal interets;

    @Column(name = "amortissement", nullable = false, precision = 12, scale = 2)
    private BigDecimal amortissement;

    @Column(name = "annuite", nullable = false, precision = 12, scale = 2)
    private BigDecimal annuite;

    @Column(name = "capital_restant", nullable = false, precision = 12, scale = 2)
    private BigDecimal capitalRestant;

    // Constructors
    public AmortissementPret() {}

    public AmortissementPret(Integer idPret, Integer periode, LocalDate dateEcheance, 
                            BigDecimal capitalDebut, BigDecimal interets, BigDecimal amortissement,
                            BigDecimal annuite, BigDecimal capitalRestant) {
        this.idPret = idPret;
        this.periode = periode;
        this.dateEcheance = dateEcheance;
        this.capitalDebut = capitalDebut;
        this.interets = interets;
        this.amortissement = amortissement;
        this.annuite = annuite;
        this.capitalRestant = capitalRestant;
    }

    // Getters & Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdPret() {
        return idPret;
    }

    public void setIdPret(Integer idPret) {
        this.idPret = idPret;
    }

    public Integer getPeriode() {
        return periode;
    }

    public void setPeriode(Integer periode) {
        this.periode = periode;
    }

    public LocalDate getDateEcheance() {
        return dateEcheance;
    }

    public void setDateEcheance(LocalDate dateEcheance) {
        this.dateEcheance = dateEcheance;
    }

    public BigDecimal getCapitalDebut() {
        return capitalDebut;
    }

    public void setCapitalDebut(BigDecimal capitalDebut) {
        this.capitalDebut = capitalDebut;
    }

    public BigDecimal getInterets() {
        return interets;
    }

    public void setInterets(BigDecimal interets) {
        this.interets = interets;
    }

    public BigDecimal getAmortissement() {
        return amortissement;
    }

    public void setAmortissement(BigDecimal amortissement) {
        this.amortissement = amortissement;
    }

    public BigDecimal getAnnuite() {
        return annuite;
    }

    public void setAnnuite(BigDecimal annuite) {
        this.annuite = annuite;
    }

    public BigDecimal getCapitalRestant() {
        return capitalRestant;
    }

    public void setCapitalRestant(BigDecimal capitalRestant) {
        this.capitalRestant = capitalRestant;
    }

    @Override
    public String toString() {
        return "AmortissementPret{" +
                "id=" + id +
                ", idPret=" + idPret +
                ", periode=" + periode +
                ", dateEcheance=" + dateEcheance +
                ", capitalDebut=" + capitalDebut +
                ", interets=" + interets +
                ", amortissement=" + amortissement +
                ", annuite=" + annuite +
                ", capitalRestant=" + capitalRestant +
                '}';
    }
}