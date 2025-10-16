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

    @Column(name = "id_transaction_envoyeur", nullable = false, length = 10)
    private String idTransactionEnvoyeur;

    @Column(name = "id_transaction_receveur", nullable = false, length = 10)
    private String idTransactionReceveur;

    @Column(name = "envoyer", nullable = false, length = 10)
    private String envoyer;

    @Column(name = "receveur", nullable = false, length = 10)
    private String receveur;

    // Constructors
    public Transfert() {}

    public Transfert(BigDecimal montant, String idTransactionEnvoyeur, String idTransactionReceveur, String envoyer, String receveur) {
        this.montant = montant;
        this.idTransactionEnvoyeur = idTransactionEnvoyeur;
        this.idTransactionReceveur = idTransactionReceveur;
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

    public String getIdTransactionEnvoyeur() {
        return idTransactionEnvoyeur;
    }

    public void setIdTransactionEnvoyeur(String idTransactionEnvoyeur) {
        this.idTransactionEnvoyeur = idTransactionEnvoyeur;
    }

    public String getIdTransactionReceveur() {
        return idTransactionReceveur;
    }

    public void setIdTransactionReceveur(String idTransactionReceveur) {
        this.idTransactionReceveur = idTransactionReceveur;
    }

    public String getEnvoyer() {
        return envoyer;
    }

    public void setEnvoyer(String envoyer) {
        this.envoyer = envoyer;
    }

    public String getReceveur() {
        return receveur;
    }

    public void setReceveur(String receveur) {
        this.receveur = receveur;
    }
}