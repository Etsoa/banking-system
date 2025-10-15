package com.example.centralizer.models.compteDepotDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private Integer idTransaction;
    private LocalDateTime dateTransaction;
    private BigDecimal montant;
    private Integer idTypeTransaction;
    private Integer idCompte;

    // Constructors
    public Transaction() {}

    public Transaction(Integer idTransaction, LocalDateTime dateTransaction, BigDecimal montant,
                      Integer idTypeTransaction, Integer idCompte) {
        this.idTransaction = idTransaction;
        this.dateTransaction = dateTransaction;
        this.montant = montant;
        this.idTypeTransaction = idTypeTransaction;
        this.idCompte = idCompte;
    }

    // Getters & Setters
    public Integer getIdTransaction() { return idTransaction; }
    public void setIdTransaction(Integer idTransaction) { this.idTransaction = idTransaction; }

    public LocalDateTime getDateTransaction() { return dateTransaction; }
    public void setDateTransaction(LocalDateTime dateTransaction) { this.dateTransaction = dateTransaction; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public Integer getIdTypeTransaction() { return idTypeTransaction; }
    public void setIdTypeTransaction(Integer idTypeTransaction) { this.idTypeTransaction = idTypeTransaction; }

    public Integer getIdCompte() { return idCompte; }
    public void setIdCompte(Integer idCompte) { this.idCompte = idCompte; }
}