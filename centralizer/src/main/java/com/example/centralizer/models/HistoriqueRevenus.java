package com.example.centralizer.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "historique_revenus")
public class HistoriqueRevenus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historique_revenus")
    private Integer id;

    @Column(name = "id_client", nullable = false)
    private Integer idClient;

    @Column(name = "montant_revenus", nullable = false, precision = 15, scale = 2)
    private BigDecimal montantRevenus;

    @Column(name = "source_revenus", length = 100)
    private String sourceRevenus;

    @Column(name = "date_enregistrement", nullable = false)
    private LocalDate dateEnregistrement;

    @Column(name = "periode_debut", nullable = false)
    private LocalDate periodeDebut;

    @Column(name = "periode_fin", nullable = false)
    private LocalDate periodeFin;

    // Constructors
    public HistoriqueRevenus() {}

    public HistoriqueRevenus(Integer idClient, BigDecimal montantRevenus, 
                           String sourceRevenus, LocalDate dateEnregistrement,
                           LocalDate periodeDebut, LocalDate periodeFin) {
        this.idClient = idClient;
        this.montantRevenus = montantRevenus;
        this.sourceRevenus = sourceRevenus;
        this.dateEnregistrement = dateEnregistrement;
        this.periodeDebut = periodeDebut;
        this.periodeFin = periodeFin;
    }

    // Getters & Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdClient() {
        return idClient;
    }

    public void setIdClient(Integer idClient) {
        this.idClient = idClient;
    }

    public BigDecimal getMontantRevenus() {
        return montantRevenus;
    }

    public void setMontantRevenus(BigDecimal montantRevenus) {
        this.montantRevenus = montantRevenus;
    }

    public String getSourceRevenus() {
        return sourceRevenus;
    }

    public void setSourceRevenus(String sourceRevenus) {
        this.sourceRevenus = sourceRevenus;
    }

    public LocalDate getDateEnregistrement() {
        return dateEnregistrement;
    }

    public void setDateEnregistrement(LocalDate dateEnregistrement) {
        this.dateEnregistrement = dateEnregistrement;
    }

    public LocalDate getPeriodeDebut() {
        return periodeDebut;
    }

    public void setPeriodeDebut(LocalDate periodeDebut) {
        this.periodeDebut = periodeDebut;
    }

    public LocalDate getPeriodeFin() {
        return periodeFin;
    }

    public void setPeriodeFin(LocalDate periodeFin) {
        this.periodeFin = periodeFin;
    }
}