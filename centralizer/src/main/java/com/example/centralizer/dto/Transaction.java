package com.example.centralizer.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO pour Transaction (reçu du ServeurCompteCourant)
 */
public class Transaction implements Serializable {
    
    private Integer idTransaction;
    private LocalDate dateTransaction;
    private BigDecimal montant;
    private Integer idCompte;
    private Integer idCompteContrepartie;
    private TypeTransaction typeTransaction;
    private StatutTransaction statutTransaction;
    
    // Constructors
    public Transaction() {
        this.dateTransaction = LocalDate.now();
        this.statutTransaction = StatutTransaction.EN_ATTENTE;
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
}
