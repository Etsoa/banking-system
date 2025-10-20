package com.example.serveurcomptecourant.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Modèle pour représenter une transaction avec les frais appliqués
 */
public class TransactionAvecFrais {
    private Integer idTransaction;
    private LocalDateTime dateTransaction;
    private BigDecimal montant;
    private Integer idTypeTransaction;
    private String idCompte;
    private String typeTransactionLibelle;
    private BigDecimal fraisAppliques;
    private String nomFrais;
    private BigDecimal montantTotal; // montant + frais pour les débits

    public TransactionAvecFrais() {}

    public TransactionAvecFrais(Transaction transaction, TypeTransaction typeTransaction) {
        this.idTransaction = transaction.getId();
        this.dateTransaction = transaction.getDateTransaction();
        this.montant = transaction.getMontant();
        this.idTypeTransaction = transaction.getIdTypeTransaction();
        this.idCompte = transaction.getIdCompte();
        this.typeTransactionLibelle = typeTransaction != null ? typeTransaction.getLibelle() : null;
        this.fraisAppliques = BigDecimal.ZERO;
        this.nomFrais = null;
        this.montantTotal = transaction.getMontant();
    }

    public TransactionAvecFrais(Transaction transaction, TypeTransaction typeTransaction, 
                               Frais frais) {
        this(transaction, typeTransaction);
        if (frais != null) {
            this.fraisAppliques = BigDecimal.valueOf(frais.getValeur());
            this.nomFrais = frais.getNom();
            // Pour les débits, le montant total inclut les frais
            if ("-".equals(typeTransaction.getSigne())) {
                this.montantTotal = transaction.getMontant().add(this.fraisAppliques);
            }
        }
    }

    // Getters and Setters
    public Integer getIdTransaction() {
        return idTransaction;
    }

    public void setIdTransaction(Integer idTransaction) {
        this.idTransaction = idTransaction;
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

    public String getTypeTransactionLibelle() {
        return typeTransactionLibelle;
    }

    public void setTypeTransactionLibelle(String typeTransactionLibelle) {
        this.typeTransactionLibelle = typeTransactionLibelle;
    }

    public BigDecimal getFraisAppliques() {
        return fraisAppliques;
    }

    public void setFraisAppliques(BigDecimal fraisAppliques) {
        this.fraisAppliques = fraisAppliques;
    }

    public String getNomFrais() {
        return nomFrais;
    }

    public void setNomFrais(String nomFrais) {
        this.nomFrais = nomFrais;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public boolean hasFrais() {
        return fraisAppliques != null && fraisAppliques.compareTo(BigDecimal.ZERO) > 0;
    }
}