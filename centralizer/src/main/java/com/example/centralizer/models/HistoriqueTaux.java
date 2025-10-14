package com.example.centralizer.models;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "historiques_taux")
public class HistoriqueTaux {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historique_taux")
    private Integer id;

    @Column(name = "date_debut", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime dateDebut;

    @Column(name = "nom", nullable = false, length = 50)
    private String nom;

    @Column(name = "valeur", nullable = false, precision = 5, scale = 2)
    private BigDecimal valeur;

    @Column(name = "actif", nullable = false)
    private Boolean actif;

    // Constructors
    public HistoriqueTaux() {}

    public HistoriqueTaux(LocalDateTime dateDebut, String nom, BigDecimal valeur, Boolean actif) {
        this.dateDebut = dateDebut;
        this.nom = nom;
        this.valeur = valeur;
        this.actif = actif;
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

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public BigDecimal getValeur() {
        return valeur;
    }

    public void setValeur(BigDecimal valeur) {
        this.valeur = valeur;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }
}