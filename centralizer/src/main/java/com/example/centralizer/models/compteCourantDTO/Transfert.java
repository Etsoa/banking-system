package com.example.centralizer.models.compteCourantDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transfert {
    private Integer idTransfert;
    private LocalDate dateTransfert;
    private BigDecimal montant;
    private String idTransactionEnvoyeur;
    private String idTransactionReceveur;
    private String envoyer;
    private String receveur;

    // Constructors
    public Transfert() {}

    public Transfert(Integer idTransfert, LocalDate dateTransfert, BigDecimal montant,
                    String idTransactionEnvoyeur, String idTransactionReceveur, 
                    String envoyer, String receveur) {
        this.idTransfert = idTransfert;
        this.dateTransfert = dateTransfert;
        this.montant = montant;
        this.idTransactionEnvoyeur = idTransactionEnvoyeur;
        this.idTransactionReceveur = idTransactionReceveur;
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

    public String getIdTransactionEnvoyeur() { return idTransactionEnvoyeur; }
    public void setIdTransactionEnvoyeur(String idTransactionEnvoyeur) { this.idTransactionEnvoyeur = idTransactionEnvoyeur; }

    public String getIdTransactionReceveur() { return idTransactionReceveur; }
    public void setIdTransactionReceveur(String idTransactionReceveur) { this.idTransactionReceveur = idTransactionReceveur; }

    public String getEnvoyer() { return envoyer; }
    public void setEnvoyer(String envoyer) { this.envoyer = envoyer; }

    public String getReceveur() { return receveur; }
    public void setReceveur(String receveur) { this.receveur = receveur; }
}