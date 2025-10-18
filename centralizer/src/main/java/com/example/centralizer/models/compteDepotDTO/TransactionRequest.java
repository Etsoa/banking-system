package com.example.centralizer.models.compteDepotDTO;

import java.math.BigDecimal;

public class TransactionRequest {
    private BigDecimal montant;
    private Integer idTypeTransaction;
    private String idCompte;

    // Constructors
    public TransactionRequest() {}

    public TransactionRequest(BigDecimal montant, Integer idTypeTransaction, String idCompte) {
        this.montant = montant;
        this.idTypeTransaction = idTypeTransaction;
        this.idCompte = idCompte;
    }

    // Getters & Setters
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public Integer getIdTypeTransaction() { return idTypeTransaction; }
    public void setIdTypeTransaction(Integer idTypeTransaction) { this.idTypeTransaction = idTypeTransaction; }

    public String getIdCompte() { return idCompte; }
    public void setIdCompte(String idCompte) { this.idCompte = idCompte; }
}