package com.example.centralizer.models.compteCourantDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transfert {
    private Integer idTransfert;
    private LocalDate dateTransfert;
    private BigDecimal montant;
    private Integer envoyer;
    private Integer receveur;

    // Constructors
    public Transfert() {}

    public Transfert(Integer idTransfert, LocalDate dateTransfert, BigDecimal montant,
                    Integer envoyer, Integer receveur) {
        this.idTransfert = idTransfert;
        this.dateTransfert = dateTransfert;
        this.montant = montant;
        this.envoyer = envoyer;
        this.receveur = receveur;
    }

    // Getters & Setters
    public Integer getIdTransfert() { return idTransfert; }
    public void setIdTransfert(Integer idTransfert) { this.idTransfert = idTransfert; }

    public LocalDate getDateTransfert() { return dateTransfert; }
    public void setDateTransfert(LocalDate dateTransfert) { this.dateTransfert = dateTransfert; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public Integer getEnvoyer() { return envoyer; }
    public void setEnvoyer(Integer envoyer) { this.envoyer = envoyer; }

    public Integer getReceveur() { return receveur; }
    public void setReceveur(Integer receveur) { this.receveur = receveur; }
}