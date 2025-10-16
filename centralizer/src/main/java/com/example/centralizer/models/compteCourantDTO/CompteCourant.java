package com.example.centralizer.models.compteCourantDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

public class CompteCourant {
    private String idCompte;
    private Integer idNum;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime dateOuverture;
    
    private Boolean decouvert;
    private Integer idClient;
    private BigDecimal solde;

    // Constructors
    public CompteCourant() {}

    public CompteCourant(String idCompte, Integer idNum, LocalDateTime dateOuverture, Boolean decouvert, 
                        Integer idClient, BigDecimal solde) {
        this.idCompte = idCompte;
        this.idNum = idNum;
        this.dateOuverture = dateOuverture;
        this.decouvert = decouvert;
        this.idClient = idClient;
        this.solde = solde;
    }

    // Getters & Setters
    public String getIdCompte() { return idCompte; }
    public void setIdCompte(String idCompte) { this.idCompte = idCompte; }

    public Integer getIdNum() { return idNum; }
    public void setIdNum(Integer idNum) { this.idNum = idNum; }

    public LocalDateTime getDateOuverture() { return dateOuverture; }
    public void setDateOuverture(LocalDateTime dateOuverture) { this.dateOuverture = dateOuverture; }

    public Boolean getDecouvert() { return decouvert; }
    public void setDecouvert(Boolean decouvert) { this.decouvert = decouvert; }

    public Integer getIdClient() { return idClient; }
    public void setIdClient(Integer idClient) { this.idClient = idClient; }

    public BigDecimal getSolde() { return solde; }
    public void setSolde(BigDecimal solde) { this.solde = solde; }
}