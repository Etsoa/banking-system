package com.example.centralizer.models.compteCourantDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private Integer idTransaction;
    private LocalDateTime dateTransaction;
    private BigDecimal montant;
    private Integer idTypeTransaction;
    private Integer idCompte;
    private Integer idTransfert;

    // Constructors
    public Transaction() {}

    public Transaction(Integer idTransaction, LocalDateTime dateTransaction, BigDecimal montant,
                      Integer idTypeTransaction, Integer idCompte, Integer idTransfert) {
        this.idTransaction = idTransaction;
        this.dateTransaction = dateTransaction;
        this.montant = montant;
        this.idTypeTransaction = idTypeTransaction;
        this.idCompte = idCompte;
        this.idTransfert = idTransfert;
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

    public Integer getIdTransfert() { return idTransfert; }
    public void setIdTransfert(Integer idTransfert) { this.idTransfert = idTransfert; }
}