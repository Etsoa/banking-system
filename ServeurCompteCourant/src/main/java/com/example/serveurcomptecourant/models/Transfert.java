package com.example.serveurcomptecourant.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transferts")
public class Transfert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transfert")
    private Integer id;

    @Column(name = "date_transfert", nullable = false)
    private LocalDate dateTransfert = LocalDate.now();

    @Column(name = "montant", nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @Column(name = "envoyer", nullable = false)
    private Integer envoyer;

    @Column(name = "receveur", nullable = false)
    private Integer receveur;

    // Constructors
    public Transfert() {}

    public Transfert(BigDecimal montant, Integer envoyer, Integer receveur) {
        this.montant = montant;
        this.envoyer = envoyer;
        this.receveur = receveur;
        this.dateTransfert = LocalDate.now();
    }

    // Getters & Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDateTransfert() {
        return dateTransfert;
    }

    public void setDateTransfert(LocalDate dateTransfert) {
        this.dateTransfert = dateTransfert;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public Integer getEnvoyer() {
        return envoyer;
    }

    public void setEnvoyer(Integer envoyer) {
        this.envoyer = envoyer;
    }

    public Integer getReceveur() {
        return receveur;
    }

    public void setReceveur(Integer receveur) {
        this.receveur = receveur;
    }
}