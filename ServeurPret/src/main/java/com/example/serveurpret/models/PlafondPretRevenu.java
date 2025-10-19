package com.example.serveurpret.models;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "plafond_pret_revenu")
public class PlafondPretRevenu implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plafond")
    private Integer idPlafond;

    @Column(name = "date_debut", nullable = false)
    private LocalDateTime dateDebut;

    @Column(name = "revenu_min", nullable = false, precision = 12, scale = 2)
    private BigDecimal revenuMin;

    @Column(name = "revenu_max", nullable = false, precision = 12, scale = 2)
    private BigDecimal revenuMax;

    @Column(name = "montant_max_pret", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantMaxPret;

    // Constructeurs
    public PlafondPretRevenu() {
    }

    public PlafondPretRevenu(LocalDateTime dateDebut, BigDecimal revenuMin, BigDecimal revenuMax, BigDecimal montantMaxPret) {
        this.dateDebut = dateDebut;
        this.revenuMin = revenuMin;
        this.revenuMax = revenuMax;
        this.montantMaxPret = montantMaxPret;
    }

    // Getters et Setters
    public Integer getIdPlafond() {
        return idPlafond;
    }

    public void setIdPlafond(Integer idPlafond) {
        this.idPlafond = idPlafond;
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

    public BigDecimal getMontantMaxPret() {
        return montantMaxPret;
    }

    public void setMontantMaxPret(BigDecimal montantMaxPret) {
        this.montantMaxPret = montantMaxPret;
    }

    @Override
    public String toString() {
        return "PlafondPretRevenu{" +
                "idPlafond=" + idPlafond +
                ", dateDebut=" + dateDebut +
                ", revenuMin=" + revenuMin +
                ", revenuMax=" + revenuMax +
                ", montantMaxPret=" + montantMaxPret +
                '}';
    }
}
