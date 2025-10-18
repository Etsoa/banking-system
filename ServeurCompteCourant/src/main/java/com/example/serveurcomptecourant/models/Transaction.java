package com.example.serveurcomptecourant.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaction")
    private Integer id;

    @Column(name = "date_transaction", nullable = false)
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime dateTransaction;

    @Column(name = "montant", nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @Column(name = "id_type_transaction", nullable = false)
    private Integer idTypeTransaction;

    @Column(name = "id_compte", nullable = false, length = 10)
    private String idCompte;

    // Constructors
    public Transaction() {}

    public Transaction(BigDecimal montant, Integer idTypeTransaction, String idCompte) {
        this.montant = montant;
        this.idTypeTransaction = idTypeTransaction;
        this.idCompte = idCompte;
    }

    // Getters & Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdTransaction() {
        return id;
    }

    public void setIdTransaction(Integer idTransaction) {
        this.id = idTransaction;
    }

    public LocalDateTime getDateTransaction() {
        return dateTransaction;
    }

    public void setDateTransaction(LocalDateTime dateTransaction) {
        this.dateTransaction = dateTransaction;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public Integer getIdTypeTransaction() {
        return idTypeTransaction;
    }

    public void setIdTypeTransaction(Integer idTypeTransaction) {
        this.idTypeTransaction = idTypeTransaction;
    }

    public String getIdCompte() {
        return idCompte;
    }

    public void setIdCompte(String idCompte) {
        this.idCompte = idCompte;
    }
}