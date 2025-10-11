package com.example.centralizer.models.pretDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Remboursement {
    private Integer idRemboursement;
    private Integer idPret;
    private BigDecimal montant;
    private LocalDate dateRemboursement;

    // Constructors
    public Remboursement() {}

    public Remboursement(Integer idRemboursement, Integer idPret, BigDecimal montant, LocalDate dateRemboursement) {
        this.idRemboursement = idRemboursement;
        this.idPret = idPret;
        this.montant = montant;
        this.dateRemboursement = dateRemboursement;
    }

    // Getters & Setters
    public Integer getIdRemboursement() { return idRemboursement; }
    public void setIdRemboursement(Integer idRemboursement) { this.idRemboursement = idRemboursement; }

    public Integer getIdPret() { return idPret; }
    public void setIdPret(Integer idPret) { this.idPret = idPret; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public LocalDate getDateRemboursement() { return dateRemboursement; }
    public void setDateRemboursement(LocalDate dateRemboursement) { this.dateRemboursement = dateRemboursement; }
}