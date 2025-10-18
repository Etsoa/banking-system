package com.example.centralizer.models.pretDTO;

import java.time.LocalDateTime;

public class Frais {
    private Integer id;
    private LocalDateTime dateDebut;
    private String nom;
    private Integer valeur;

    // Constructors
    public Frais() {}

    public Frais(Integer id, LocalDateTime dateDebut, String nom, Integer valeur) {
        this.id = id;
        this.dateDebut = dateDebut;
        this.nom = nom;
        this.valeur = valeur;
    }

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Integer getValeur() { return valeur; }
    public void setValeur(Integer valeur) { this.valeur = valeur; }

    @Override
    public String toString() {
        return "Frais{" +
                "id=" + id +
                ", dateDebut=" + dateDebut +
                ", nom='" + nom + '\'' +
                ", valeur=" + valeur +
                '}';
    }
}