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
@Table(name = "prets")
public class Pret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pret")
    private Integer id;

    @Column(name = "montant", nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @Column(name = "duree_periode", nullable = false)
    private Integer dureePeriode;

    @Column(name = "date_debut", nullable = false)
    @JsonbDateFormat("yyyy-MM-dd")
    private LocalDate dateDebut;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Column(name = "id_statut_pret", nullable = false)
    private Integer idStatutPret;

    @Column(name = "id_modalite", nullable = false)
    private Integer idModalite;

    @Column(name = "id_type_remboursement", nullable = false)
    private Integer idTypeRemboursement;

    // Constructors
    public Pret() {}

    public Pret(BigDecimal montant, Integer dureePeriode, LocalDate dateDebut, String clientId,
                Integer idStatutPret, Integer idModalite, Integer idTypeRemboursement) {
        this.montant = montant;
        this.dureePeriode = dureePeriode;
        this.dateDebut = dateDebut;
        this.clientId = clientId;
        this.idStatutPret = idStatutPret;
        this.idModalite = idModalite;
        this.idTypeRemboursement = idTypeRemboursement;
    }

    // Getters & Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public Integer getDureePeriode() {
        return dureePeriode;
    }

    public void setDureePeriode(Integer dureePeriode) {
        this.dureePeriode = dureePeriode;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Integer getIdStatutPret() {
        return idStatutPret;
    }

    public void setIdStatutPret(Integer idStatutPret) {
        this.idStatutPret = idStatutPret;
    }

    public Integer getIdModalite() {
        return idModalite;
    }

    public void setIdModalite(Integer idModalite) {
        this.idModalite = idModalite;
    }

    public Integer getIdTypeRemboursement() {
        return idTypeRemboursement;
    }

    public void setIdTypeRemboursement(Integer idTypeRemboursement) {
        this.idTypeRemboursement = idTypeRemboursement;
    }

    @Override
    public String toString() {
        return "Pret{" +
                "id=" + id +
                ", montant=" + montant +
                ", dureePeriode=" + dureePeriode +
                ", dateDebut=" + dateDebut +
                ", clientId=" + clientId +
                ", idStatutPret=" + idStatutPret +
                ", idModalite=" + idModalite +
                ", idTypeRemboursement=" + idTypeRemboursement +
                '}';
    }
}
