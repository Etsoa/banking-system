package com.example.serveurcomptecourant.models;

import jakarta.persistence.*;

@Entity
@Table(name = "compte_courant")
public class CompteCourant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double solde;
    private String titulaire;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Double getSolde() { return solde; }
    public void setSolde(Double solde) { this.solde = solde; }
    public String getTitulaire() { return titulaire; }
    public void setTitulaire(String titulaire) { this.titulaire = titulaire; }
}
