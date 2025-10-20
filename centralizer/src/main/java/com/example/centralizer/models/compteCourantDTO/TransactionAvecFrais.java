package com.example.centralizer.models.compteCourantDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO pour représenter une transaction avec les frais appliqués
 */
public class TransactionAvecFrais {
    
    @JsonProperty("idTransaction")
    private Integer idTransaction;
    
    @JsonProperty("dateTransaction")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateTransaction;
    
    @JsonProperty("montant")
    private BigDecimal montant;
    
    @JsonProperty("idTypeTransaction")
    private Integer idTypeTransaction;
    
    @JsonProperty("idCompte")
    private String idCompte;
    
    @JsonProperty("typeTransactionLibelle")
    private String typeTransactionLibelle;
    
    @JsonProperty("fraisAppliques")
    private BigDecimal fraisAppliques = BigDecimal.ZERO;
    
    @JsonProperty("nomFrais")
    private String nomFrais;
    
    @JsonProperty("montantTotal")
    private BigDecimal montantTotal = BigDecimal.ZERO;

    // Constructeurs
    public TransactionAvecFrais() {}

    // Getters et Setters
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