package com.example.centralizer.dto.echange;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO pour Echange (Serializable pour la sérialisation EJB)
 */
public class Echange implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nom;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private BigDecimal valeur;

    public Echange() {
    }

    public Echange(String nom, LocalDate dateDebut, LocalDate dateFin, BigDecimal valeur) {
        this.nom = nom;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.valeur = valeur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public BigDecimal getValeur() {
        return valeur;
    }

    public void setValeur(BigDecimal valeur) {
        this.valeur = valeur;
    }
    
    /**
     * Vérifie si cet échange est actif à une date donnée
     */
    public boolean estActif(LocalDate date) {
        if (dateDebut != null && date.isBefore(dateDebut)) {
            return false;
        }
        if (dateFin != null && date.isAfter(dateFin)) {
            return false;
        }
        return true;
    }
    
    /**
     * Vérifie si cet échange est actuellement actif
     */
    public boolean estActif() {
        return estActif(LocalDate.now());
    }

    @Override
    public String toString() {
        return "Echange{" +
                "nom='" + nom + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", valeur=" + valeur +
                '}';
    }
}
