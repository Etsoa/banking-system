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
@Table(name = "taux_interet")
public class TauxInteret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_taux")
    private Integer id;

    @Column(name = "taux_annuel", nullable = false, precision = 5, scale = 2)
    private BigDecimal tauxAnnuel;

    @Column(name = "date_debut", nullable = false)
    @JsonbDateFormat("yyyy-MM-dd")
    private LocalDate dateDebut;

    // Constructors
    public TauxInteret() {}

    public TauxInteret(BigDecimal tauxAnnuel, LocalDate dateDebut) {
        this.tauxAnnuel = tauxAnnuel;
        this.dateDebut = dateDebut;
    }

    // Getters & Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getTauxAnnuel() {
        return tauxAnnuel;
    }

    public void setTauxAnnuel(BigDecimal tauxAnnuel) {
        this.tauxAnnuel = tauxAnnuel;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    @Override
    public String toString() {
        return "TauxInteret{" +
                "id=" + id +
                ", tauxAnnuel=" + tauxAnnuel +
                ", dateDebut=" + dateDebut +
                '}';
    }
}