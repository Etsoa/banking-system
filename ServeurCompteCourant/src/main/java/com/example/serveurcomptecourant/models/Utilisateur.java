package com.example.serveurcomptecourant.models;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "utilisateurs")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisateur")
    private Integer idUtilisateur;

    @Column(name = "nom_utilisateur", length = 50, nullable = false, unique = true)
    private String nomUtilisateur;

    @Column(name = "mot_de_passe", length = 100, nullable = false)
    @JsonbTransient
    private String motDePasse;

    @Column(name = "id_direction")
    private Integer idDirection;

    @Column(name = "role_utilisateur")
    private Integer roleUtilisateur;

    // Navigation property
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_direction", insertable = false, updatable = false)
    @JsonbTransient
    private Direction direction;

    // Constructors
    public Utilisateur() {}

    public Utilisateur(String nomUtilisateur, String motDePasse, Integer idDirection, Integer roleUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
        this.motDePasse = motDePasse;
        this.idDirection = idDirection;
        this.roleUtilisateur = roleUtilisateur;
    }

    // Getters & Setters
    public Integer getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Integer idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public Integer getIdDirection() {
        return idDirection;
    }

    public void setIdDirection(Integer idDirection) {
        this.idDirection = idDirection;
    }

    public Integer getRoleUtilisateur() {
        return roleUtilisateur;
    }

    public void setRoleUtilisateur(Integer roleUtilisateur) {
        this.roleUtilisateur = roleUtilisateur;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}