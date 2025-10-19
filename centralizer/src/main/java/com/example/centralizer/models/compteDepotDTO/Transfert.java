package com.example.centralizer.models.compteDepotDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonFormat;

public class Transfert {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer idTransfert;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateTransfert;
    private String idTransactionEnvoyeur;
    private String idTransactionReceveur;
    private BigDecimal montant;
    private String envoyer;
    private String receveur;

    // Constructors
    public Transfert() {}

    public Transfert(Integer idTransfert, LocalDate dateTransfert, String idTransactionEnvoyeur, 
                    String idTransactionReceveur, BigDecimal montant, String envoyer, String receveur) {
        this.idTransfert = idTransfert;
        this.dateTransfert = dateTransfert;
        this.idTransactionEnvoyeur = idTransactionEnvoyeur;
        this.idTransactionReceveur = idTransactionReceveur;
        this.montant = montant;
        this.envoyer = envoyer;
        this.receveur = receveur;
    }

    // Getters & Setters
    public Integer getIdTransfert() { return idTransfert; }
    public void setIdTransfert(Integer idTransfert) { this.idTransfert = idTransfert; }

    public LocalDate getDateTransfert() { return dateTransfert; }
    public void setDateTransfert(LocalDate dateTransfert) { this.dateTransfert = dateTransfert; }

    public String getIdTransactionEnvoyeur() { return idTransactionEnvoyeur; }
    public void setIdTransactionEnvoyeur(String idTransactionEnvoyeur) { this.idTransactionEnvoyeur = idTransactionEnvoyeur; }

    public String getIdTransactionReceveur() { return idTransactionReceveur; }
    public void setIdTransactionReceveur(String idTransactionReceveur) { this.idTransactionReceveur = idTransactionReceveur; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public String getEnvoyer() { return envoyer; }
    public void setEnvoyer(String envoyer) { this.envoyer = envoyer; }

    public String getReceveur() { return receveur; }
    public void setReceveur(String receveur) { this.receveur = receveur; }
}