package com.example.serveurcomptecourant.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.json.bind.annotation.JsonbDateFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "historiques_solde")
public class HistoriqueSolde {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historique_solde")
    private Integer id;

    @Column(name = "montant", nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @Column(name = "date_changement", nullable = false)
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime dateChangement = LocalDateTime.now();

    @Column(name = "id_compte", nullable = false, length = 10)
    private String idCompte;

    @Column(name = "id_transaction", nullable = false)
    private Integer idTransaction;

    // Constructors
    public HistoriqueSolde() {}

    public HistoriqueSolde(BigDecimal montant, String idCompte, Integer idTransaction) {
        this.montant = montant;
        this.idCompte = idCompte;
        this.idTransaction = idTransaction;
        this.dateChangement = LocalDateTime.now();
    }

    // Getters & Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public LocalDateTime getDateChangement() {
        return dateChangement;
    }

    public void setDateChangement(LocalDateTime dateChangement) {
        this.dateChangement = dateChangement;
    }

    public String getIdCompte() {
        return idCompte;
    }

    public void setIdCompte(String idCompte) {
        this.idCompte = idCompte;
    }

    public Integer getIdTransaction() {
        return idTransaction;
    }

    public void setIdTransaction(Integer idTransaction) {
        this.idTransaction = idTransaction;
    }

    @Override
    public String toString() {
        return "HistoriqueSolde{" +
                "id=" + id +
                ", montant=" + montant +
                ", dateChangement=" + dateChangement +
                ", idCompte=" + idCompte +
                ", idTransaction=" + idTransaction +
                '}';
    }
}