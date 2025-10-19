package com.example.centralizer.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "historiques_revenus")
public class HistoriqueRevenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historique_revenus")
    private Integer id;

    @Column(name = "date_changement", nullable = false)
    private LocalDate dateChangement;

    @Column(name = "valeur", nullable = false, precision = 15, scale = 2)
    private BigDecimal valeur;

    @Column(name = "id_client", nullable = false)
    private Integer idClient;

    // Constructeurs
    public HistoriqueRevenu() {
    }

    public HistoriqueRevenu(LocalDate dateChangement, BigDecimal valeur, Integer idClient) {
        this.dateChangement = dateChangement;
        this.valeur = valeur;
        this.idClient = idClient;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDateChangement() {
        return dateChangement;
    }

    public void setDateChangement(LocalDate dateChangement) {
        this.dateChangement = dateChangement;
    }

    public BigDecimal getValeur() {
        return valeur;
    }

    public void setValeur(BigDecimal valeur) {
        this.valeur = valeur;
    }

    public Integer getIdClient() {
        return idClient;
    }

    public void setIdClient(Integer idClient) {
        this.idClient = idClient;
    }

    @Override
    public String toString() {
        return "HistoriqueRevenu{" +
                "id=" + id +
                ", dateChangement=" + dateChangement +
                ", valeur=" + valeur +
                ", idClient=" + idClient +
                '}';
    }
}