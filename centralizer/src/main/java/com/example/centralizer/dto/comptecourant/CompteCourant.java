package com.example.centralizer.dto.comptecourant;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO pour CompteCourant (Serializable pour la sérialisation EJB)
 */
public class CompteCourant implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idCompte;
    private BigDecimal solde;

    public CompteCourant() {}

    public CompteCourant(Integer idCompte, BigDecimal solde) {
        this.idCompte = idCompte;
        this.solde = solde;
    }

    public Integer getIdCompte() {
        return idCompte;
    }

    public void setIdCompte(Integer idCompte) {
        this.idCompte = idCompte;
    }

    public BigDecimal getSolde() {
        return solde;
    }

    public void setSolde(BigDecimal solde) {
        this.solde = solde;
    }

    @Override
    public String toString() {
        return "CompteCourant{" +
                "idCompte=" + idCompte +
                ", solde=" + solde +
                '}';
    }
}
