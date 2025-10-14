package com.example.serveurpret.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.json.bind.annotation.JsonbDateFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "remboursements")
public class Remboursement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_remboursement")
    private Integer id;

    @Column(name = "date_paiement", nullable = false)
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime datePaiement;

    @Column(name = "montant", nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @Column(name = "Id_methode_remboursement", nullable = false)
    private Integer idMethodeRemboursement;

    @Column(name = "id_pret", nullable = false)
    private Integer idPret;

    // Constructors
    public Remboursement() {}

    public Remboursement(LocalDateTime datePaiement, BigDecimal montant, 
                        Integer idMethodeRemboursement, Integer idPret) {
        this.datePaiement = datePaiement;
        this.montant = montant;
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

    public LocalDateTime getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDateTime datePaiement) {
        this.datePaiement = datePaiement;
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