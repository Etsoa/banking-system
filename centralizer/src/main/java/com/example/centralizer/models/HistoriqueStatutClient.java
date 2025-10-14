package com.example.centralizer.models;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@Entity
@Table(name = "historiques_statut_client")
public class HistoriqueStatutClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historique_statut_client")
    private Integer id;

    @Column(name = "date_changement", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime dateChangement;

    @Column(name = "id_client", nullable = false)
    private Integer idClient;

    @Column(name = "statut", nullable = false, length = 50)
    private String statut;

    // Constructors
    public HistoriqueStatutClient() {
        this.dateChangement = LocalDateTime.now();
    }

    public HistoriqueStatutClient(Integer idClient, String statut) {
        this.idClient = idClient;
        this.statut = statut;
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

    public Integer getIdClient() {
        return idClient;
    }

    public void setIdClient(Integer idClient) {
        this.idClient = idClient;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "HistoriqueStatutClient{" +
                "id=" + id +
                ", dateChangement=" + dateChangement +
                ", idClient=" + idClient +
                ", statut='" + statut + '\'' +
                '}';
    }
}