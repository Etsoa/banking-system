package com.example.centralizer.models.pretDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TauxInteret {
    private Integer id;
    private BigDecimal tauxAnnuel;
    private LocalDate dateDebut;

    // Constructors
    public TauxInteret() {}

    public TauxInteret(Integer id, BigDecimal tauxAnnuel, LocalDate dateDebut) {
        this.id = id;
        this.tauxAnnuel = tauxAnnuel;
        this.dateDebut = dateDebut;
    }

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public BigDecimal getTauxAnnuel() { return tauxAnnuel; }
    public void setTauxAnnuel(BigDecimal tauxAnnuel) { this.tauxAnnuel = tauxAnnuel; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    @Override
    public String toString() {
        return "TauxInteret{" +
                "id=" + id +
                ", tauxAnnuel=" + tauxAnnuel +
                ", dateDebut=" + dateDebut +
                '}';
    }
}