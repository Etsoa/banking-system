package com.example.centralizer.dto.comptecourant;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO pour Transaction (Serializable pour la sérialisation EJB)
 */
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idTransaction;
    private LocalDate dateTransaction;
    private BigDecimal montant;
    private Integer idCompte;
    private Integer idCompteContrepartie;
    private TypeTransaction typeTransaction;
    private StatutTransaction statutTransaction;

    public Transaction() {}

    public Transaction(Integer idTransaction, LocalDate dateTransaction, BigDecimal montant,
                      Integer idCompte, TypeTransaction typeTransaction, 
                      StatutTransaction statutTransaction) {
        this.idTransaction = idTransaction;
        this.dateTransaction = dateTransaction;
        this.montant = montant;
        this.idCompte = idCompte;
        this.typeTransaction = typeTransaction;
        this.statutTransaction = statutTransaction;
    }

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

    public Integer getIdCompteContrepartie() {
        return idCompteContrepartie;
    }

    public void setIdCompteContrepartie(Integer idCompteContrepartie) {
        this.idCompteContrepartie = idCompteContrepartie;
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

    @Override
    public String toString() {
        return "Transaction{" +
                "idTransaction=" + idTransaction +
                ", dateTransaction=" + dateTransaction +
                ", montant=" + montant +
                ", idCompte=" + idCompte +
                ", typeTransaction=" + typeTransaction +
                ", statutTransaction=" + statutTransaction +
                '}';
    }
}
