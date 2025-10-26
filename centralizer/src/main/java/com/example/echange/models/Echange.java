package com.example.echange.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Echange implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nom;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private BigDecimal valeur;

    public Echange() {}

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public BigDecimal getValeur() { return valeur; }
    public void setValeur(BigDecimal valeur) { this.valeur = valeur; }

    public boolean estActif(LocalDate date) {
        if (dateDebut != null && date.isBefore(dateDebut)) {
            return false;
        }
        if (dateFin != null && date.isAfter(dateFin)) {
            return false;
        }
        return true;
    }
}
