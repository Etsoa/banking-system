package com.example.serveurpret.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "prets")
public class Pret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pret")
    private Integer id;

    @Column(name = "montant", nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @Column(name = "duree_mois", nullable = false)
    private Integer dureeMois;

    @Column(name = "date_debut", nullable = false)
    private LocalDateTime dateDebut;

    @Column(name = "Id_statut_pret", nullable = false)
    private Integer idStatutPret;

    @Column(name = "id_modalite", nullable = false)
    private Integer idModalite;

    @Column(name = "Id_type_remboursement", nullable = false)
    private Integer idTypeRemboursement;

    // Constructors
    public Pret() {}

    public Pret(BigDecimal montant, Integer dureeMois, LocalDateTime dateDebut, 
                Integer idStatutPret, Integer idModalite, Integer idTypeRemboursement) {
        this.montant = montant;
        this.dureeMois = dureeMois;
        this.dateDebut = dateDebut;
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

    public Integer getDureeMois() {
        return dureeMois;
    }

    public void setDureeMois(Integer dureeMois) {
        this.dureeMois = dureeMois;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
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
                ", dureeMois=" + dureeMois +
                ", dateDebut=" + dateDebut +
                ", idStatutPret=" + idStatutPret +
                ", idModalite=" + idModalite +
                ", idTypeRemboursement=" + idTypeRemboursement +
                '}';
    }
}
