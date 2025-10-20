package com.example.serveurcomptecourant.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "actions_roles")
public class ActionRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_action_role")
    private Integer idActionRole;

    @Column(name = "nom_table", length = 20)
    private String nomTable;

    @Column(name = "nom_action", length = 20)
    private String nomAction;

    @Column(name = "role_minimum")
    private Integer roleMinimum;

    // Constructors
    public ActionRole() {}

    public ActionRole(String nomTable, String nomAction, Integer roleMinimum) {
        this.nomTable = nomTable;
        this.nomAction = nomAction;
        this.roleMinimum = roleMinimum;
    }

    // Getters & Setters
    public Integer getIdActionRole() {
        return idActionRole;
    }

    public void setIdActionRole(Integer idActionRole) {
        this.idActionRole = idActionRole;
    }

    public String getNomTable() {
        return nomTable;
    }

    public void setNomTable(String nomTable) {
        this.nomTable = nomTable;
    }

    public String getNomAction() {
        return nomAction;
    }

    public void setNomAction(String nomAction) {
        this.nomAction = nomAction;
    }

    public Integer getRoleMinimum() {
        return roleMinimum;
    }

    public void setRoleMinimum(Integer roleMinimum) {
        this.roleMinimum = roleMinimum;
    }
}