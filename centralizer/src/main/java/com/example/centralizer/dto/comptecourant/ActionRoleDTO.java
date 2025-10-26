package com.example.centralizer.dto.comptecourant;

import java.io.Serializable;

/**
 * DTO représentant une action/permission d'un rôle
 */
public class ActionRoleDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idActionRole;
    private String nomTable;
    private String nomAction;
    private Integer roleMinimum;

    public ActionRoleDTO() {}

    public ActionRoleDTO(Integer idActionRole, String nomTable, String nomAction, Integer roleMinimum) {
        this.idActionRole = idActionRole;
        this.nomTable = nomTable;
        this.nomAction = nomAction;
        this.roleMinimum = roleMinimum;
    }

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

    @Override
    public String toString() {
        return nomTable + ":" + nomAction + " (role>=" + roleMinimum + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActionRoleDTO that = (ActionRoleDTO) o;
        return nomTable.equals(that.nomTable) && nomAction.equals(that.nomAction);
    }

    @Override
    public int hashCode() {
        return (nomTable + ":" + nomAction).hashCode();
    }
}
