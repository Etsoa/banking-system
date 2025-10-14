package com.example.centralizer.models.compteCourantDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

public class CompteCourant {
    private Integer id;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime dateOuverture;
    
    private Boolean decouvert;
    private Integer idClient;
    private BigDecimal solde;

    // Constructors
    public CompteCourant() {}

    public CompteCourant(Integer id, LocalDateTime dateOuverture, Boolean decouvert, 
                        Integer idClient, BigDecimal solde) {
        this.id = id;
        this.dateOuverture = dateOuverture;
        this.decouvert = decouvert;
        this.idClient = idClient;
        this.solde = solde;
    }

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDateTime getDateOuverture() { return dateOuverture; }
    public void setDateOuverture(LocalDateTime dateOuverture) { this.dateOuverture = dateOuverture; }

    public Boolean getDecouvert() { return decouvert; }
    public void setDecouvert(Boolean decouvert) { this.decouvert = decouvert; }

    public Integer getIdClient() { return idClient; }
    public void setIdClient(Integer idClient) { this.idClient = idClient; }

    public BigDecimal getSolde() { return solde; }
    public void setSolde(BigDecimal solde) { this.solde = solde; }
}