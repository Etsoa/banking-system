package com.example.serveurcomptecourant.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "comptes")
public class CompteCourant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_num")
    private Integer idNum;

    @Generated(GenerationTime.INSERT)
    @Column(name = "id_compte", length = 10, insertable = false, updatable = false)
    private String idCompte;

    @Column(name = "date_ouverture", nullable = false)
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime dateOuverture = LocalDateTime.now();

    @Column(name = "decouvert", nullable = false)
    private Boolean decouvert;

    @Column(name = "id_client", nullable = false)
    private Integer idClient;

    @Column(name = "solde", nullable = false, precision = 12, scale = 2)
    private BigDecimal solde = BigDecimal.ZERO;

    // Constructors

    public CompteCourant() {}

    public CompteCourant(Boolean decouvert, Integer idClient, BigDecimal solde) {
        this.decouvert = decouvert;
        this.idClient = idClient;
        this.solde = solde;
        this.dateOuverture = LocalDateTime.now();
    }

    // Getters & Setters

    public String getIdCompte() {
        return idCompte;
    }

    public void setIdCompte(String idCompte) {
        this.idCompte = idCompte;
    }

    public Integer getIdNum() {
        return idNum;
    }

    public void setIdNum(Integer idNum) {
        this.idNum = idNum;
    }

    public LocalDateTime getDateOuverture() {
        return dateOuverture;
    }

    public void setDateOuverture(LocalDateTime dateOuverture) {
        this.dateOuverture = dateOuverture;
    }

    public Boolean getDecouvert() {
        return decouvert;
    }

    public void setDecouvert(Boolean decouvert) {
        this.decouvert = decouvert;
    }

    public Integer getIdClient() {
        return idClient;
    }

    public void setIdClient(Integer idClient) {
        this.idClient = idClient;
    }

    public BigDecimal getSolde() {
        return solde;
    }

    public void setSolde(BigDecimal solde) {
        this.solde = solde;
    }
}
