package com.example.serveurcomptecourant.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "transactions")
public class Transaction {

    public enum TypeTransaction {
        debit, credit, virement
    }

    public enum StatutTransaction {
        en_attente, confirmee, refusee
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaction")
    private Integer idTransaction;

    @Column(name = "date_transaction", nullable = false)
    @JsonbDateFormat("yyyy-MM-dd")
    private LocalDate dateTransaction = LocalDate.now();

    @Column(name = "montant", nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @Column(name = "id_compte", nullable = false)
    private Integer idCompte;

    @Column(name = "id_compte_contrpartie")
    private Integer idCompteContrpartie;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_transaction", nullable = false, length = 20)
    private TypeTransaction typeTransaction;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_transaction", nullable = false, length = 20)
    private StatutTransaction statutTransaction = StatutTransaction.en_attente;

    // Constructors
    public Transaction() {}

    public Transaction(BigDecimal montant, Integer idCompte, Integer idCompteContrpartie, TypeTransaction typeTransaction) {
        this.montant = montant;
        this.idCompte = idCompte;
        this.idCompteContrpartie = idCompteContrpartie;
        this.typeTransaction = typeTransaction;
        this.statutTransaction = StatutTransaction.en_attente;
        this.dateTransaction = LocalDate.now();
    }

    // Getters & Setters
    public Integer getIdTransaction() {
        return idTransaction;
    }

    public void setIdTransaction(Integer idTransaction) {
        this.idTransaction = idTransaction;
    }

    public LocalDate getDateTransaction() {
        return dateTransaction;
    }

    public void setDateTransaction(LocalDate dateTransaction) {
        this.dateTransaction = dateTransaction;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public Integer getIdCompte() {
        return idCompte;
    }

    public void setIdCompte(Integer idCompte) {
        this.idCompte = idCompte;
    }

    public Integer getIdCompteContrpartie() {
        return idCompteContrpartie;
    }

    public void setIdCompteContrpartie(Integer idCompteContrpartie) {
        this.idCompteContrpartie = idCompteContrpartie;
    }

    public TypeTransaction getTypeTransaction() {
        return typeTransaction;
    }

    public void setTypeTransaction(TypeTransaction typeTransaction) {
        this.typeTransaction = typeTransaction;
    }

    public StatutTransaction getStatutTransaction() {
        return statutTransaction;
    }

    public void setStatutTransaction(StatutTransaction statutTransaction) {
        this.statutTransaction = statutTransaction;
    }
}