package com.example.comptecourant.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "actions_roles")
public class ActionRole implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idActionRole;
    private String nomTable;
    private String nomAction;
    private Integer roleMinimum;

    public ActionRole() {}

    public Integer getIdActionRole() { return idActionRole; }
    public void setIdActionRole(Integer idActionRole) { this.idActionRole = idActionRole; }

    public String getNomTable() { return nomTable; }
    public void setNomTable(String nomTable) { this.nomTable = nomTable; }

    public String getNomAction() { return nomAction; }
    public void setNomAction(String nomAction) { this.nomAction = nomAction; }

    public Integer getRoleMinimum() { return roleMinimum; }
    public void setRoleMinimum(Integer roleMinimum) { this.roleMinimum = roleMinimum; }
}
