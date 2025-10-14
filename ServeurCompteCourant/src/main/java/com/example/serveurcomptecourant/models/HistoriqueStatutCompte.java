package com.example.serveurcomptecourant.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.json.bind.annotation.JsonbDateFormat;
import java.time.LocalDateTime;

@Entity
@Table(name = "historiques_statut_compte")
public class HistoriqueStatutCompte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historique_statut_compte")
    private Integer id;

    @Column(name = "date_changement", nullable = false)
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime dateChangement = LocalDateTime.now();

    @Column(name = "id_compte", nullable = false)
    private Integer idCompte;

    @Column(name = "Id_type_statut_compte", nullable = false)
    private Integer idTypeStatutCompte;

    // Constructors
    public HistoriqueStatutCompte() {}

    public HistoriqueStatutCompte(Integer idCompte, Integer idTypeStatutCompte) {
        this.idCompte = idCompte;
        this.idTypeStatutCompte = idTypeStatutCompte;
        this.dateChangement = LocalDateTime.now();
    }

    // Getters & Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getDateChangement() {
        return dateChangement;
    }

    public void setDateChangement(LocalDateTime dateChangement) {
        this.dateChangement = dateChangement;
    }

    public Integer getIdCompte() {
        return idCompte;
    }

    public void setIdCompte(Integer idCompte) {
        this.idCompte = idCompte;
    }

    public Integer getIdTypeStatutCompte() {
        return idTypeStatutCompte;
    }

    public void setIdTypeStatutCompte(Integer idTypeStatutCompte) {
        this.idTypeStatutCompte = idTypeStatutCompte;
    }

    @Override
    public String toString() {
        return "HistoriqueStatutCompte{" +
                "id=" + id +
                ", dateChangement=" + dateChangement +
                ", idCompte=" + idCompte +
                ", idTypeStatutCompte=" + idTypeStatutCompte +
                '}';
    }
}