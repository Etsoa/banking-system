package com.example.centralizer.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historique_role_utilisateur")
public class HistoriqueRoleUtilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historique_role")
    private Integer id;

    @Column(name = "id_utilisateur", nullable = false)
    private Integer idUtilisateur;

    @Column(name = "id_role", nullable = false)
    private Integer idRole;

    @Column(name = "date_attribution", nullable = false)
    private LocalDateTime dateAttribution;

    @Column(name = "date_revocation")
    private LocalDateTime dateRevocation;

    @Column(name = "attribue_par", nullable = false)
    private Integer attribuePar;

    // Constructors
    public HistoriqueRoleUtilisateur() {}

    public HistoriqueRoleUtilisateur(Integer idUtilisateur, Integer idRole, 
                                   LocalDateTime dateAttribution, Integer attribuePar) {
        this.idUtilisateur = idUtilisateur;
        this.idRole = idRole;
        this.dateAttribution = dateAttribution;
        this.attribuePar = attribuePar;
    }

    // Getters & Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Integer idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public Integer getIdRole() {
        return idRole;
    }

    public void setIdRole(Integer idRole) {
        this.idRole = idRole;
    }

    public LocalDateTime getDateAttribution() {
        return dateAttribution;
    }

    public void setDateAttribution(LocalDateTime dateAttribution) {
        this.dateAttribution = dateAttribution;
    }

    public LocalDateTime getDateRevocation() {
        return dateRevocation;
    }

    public void setDateRevocation(LocalDateTime dateRevocation) {
        this.dateRevocation = dateRevocation;
    }

    public Integer getAttribuePar() {
        return attribuePar;
    }

    public void setAttribuePar(Integer attribuePar) {
        this.attribuePar = attribuePar;
    }
}