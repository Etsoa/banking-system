package com.example.serveurpret.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.json.bind.annotation.JsonbDateFormat;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "remboursements")
public class Remboursement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_remboursement")
    private Integer id;

    @Column(name = "date_paiement")
    @JsonbDateFormat("yyyy-MM-dd")
    private LocalDate datePaiement;

    @Column(name = "montant", nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @Column(name = "jours_retard")
    private Integer joursRetard = 0;

    @Column(name = "id_statut_remboursement")
    private Integer idStatutRemboursement;

    @Column(name = "id_methode_remboursement", nullable = false)
    private Integer idMethodeRemboursement;

    @Column(name = "id_pret", nullable = false)
    private Integer idPret;

    // Constructors
    public Remboursement() {}

    public Remboursement(LocalDate datePaiement, BigDecimal montant, Integer joursRetard,
                        Integer idStatutRemboursement, Integer idMethodeRemboursement, Integer idPret) {
        this.datePaiement = datePaiement;
        this.montant = montant;
        this.joursRetard = joursRetard;
        this.idStatutRemboursement = idStatutRemboursement;
        this.idMethodeRemboursement = idMethodeRemboursement;
        this.idPret = idPret;
    }

    // Getters & Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDate datePaiement) {
        this.datePaiement = datePaiement;
    }

    public Integer getJoursRetard() {
        return joursRetard;
    }

    public void setJoursRetard(Integer joursRetard) {
        this.joursRetard = joursRetard;
    }

    public Integer getIdStatutRemboursement() {
        return idStatutRemboursement;
    }

    public void setIdStatutRemboursement(Integer idStatutRemboursement) {
        this.idStatutRemboursement = idStatutRemboursement;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public Integer getIdMethodeRemboursement() {
        return idMethodeRemboursement;
    }

    public void setIdMethodeRemboursement(Integer idMethodeRemboursement) {
        this.idMethodeRemboursement = idMethodeRemboursement;
    }

    public Integer getIdPret() {
        return idPret;
    }

    public void setIdPret(Integer idPret) {
        this.idPret = idPret;
    }
}