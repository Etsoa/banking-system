package com.example.comptecourant.models;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Table(name = "utilisateurs")
public class Utilisateur implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUtilisateur;
    private String nomUtilisateur;
    private String motDePasse;
    private Integer roleUtilisateur;
    
    @ManyToOne
    @JoinColumn(name = "id_direction", foreignKey = @ForeignKey(name = "fk_utilisateur_direction"))
    private Direction direction;

    public Utilisateur() {}

    public Integer getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(Integer idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public String getNomUtilisateur() { return nomUtilisateur; }
    public void setNomUtilisateur(String nomUtilisateur) { this.nomUtilisateur = nomUtilisateur; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public Integer getRoleUtilisateur() { return roleUtilisateur; }
    public void setRoleUtilisateur(Integer roleUtilisateur) { this.roleUtilisateur = roleUtilisateur; }
    
    public Direction getDirection() { return direction; }
    public void setDirection(Direction direction) { this.direction = direction; }
}
