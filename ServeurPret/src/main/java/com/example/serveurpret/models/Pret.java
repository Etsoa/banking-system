package com.example.serveurpret.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prets")
public class Pret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pret")
    private Integer id;

    @Column(name = "date_pret", nullable = false)
    private LocalDateTime datePret;

    @Column(name = "montant", nullable = false)
    private Long montant;

    @Column(name = "nb_mois_retour_prevu", nullable = false)
    private Integer nbMoisRetourPrevu;

    @Column(name = "id_type_statu_pret", nullable = false)
    private Integer idTypeStatuPret;

    @Column(name = "id_type_remboursement", nullable = false)
    private Integer idTypeRemboursement;

    @Column(name = "id_modalite", nullable = false)
    private Integer idModalite;

    @Column(name = "id_client", nullable = false)
    private Integer idClient;

    // Getters & Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getDatePret() {
        return datePret;
    }

    public void setDatePret(LocalDateTime datePret) {
        this.datePret = datePret;
    }

    public Long getMontant() {
        return montant;
    }

    public void setMontant(Long montant) {
        this.montant = montant;
    }

    public Integer getNbMoisRetourPrevu() {
        return nbMoisRetourPrevu;
    }

    public void setNbMoisRetourPrevu(Integer nbMoisRetourPrevu) {
        this.nbMoisRetourPrevu = nbMoisRetourPrevu;
    }

    public Integer getIdTypeStatuPret() {
        return idTypeStatuPret;
    }

    public void setIdTypeStatuPret(Integer idTypeStatuPret) {
        this.idTypeStatuPret = idTypeStatuPret;
    }

    public Integer getIdTypeRemboursement() {
        return idTypeRemboursement;
    }

    public void setIdTypeRemboursement(Integer idTypeRemboursement) {
        this.idTypeRemboursement = idTypeRemboursement;
    }

    public Integer getIdModalite() {
        return idModalite;
    }

    public void setIdModalite(Integer idModalite) {
        this.idModalite = idModalite;
    }

    public Integer getIdClient() {
        return idClient;
    }

    public void setIdClient(Integer idClient) {
        this.idClient = idClient;
    }
}
