package com.example.centralizer.models.compteDepotDTO;

/**
 * DTO pour les requêtes de transactions par compte et type
 */
public class TransactionsByCompteAndTypeRequest {
    private String compteId;
    private Integer typeId;

    // Constructeurs
    public TransactionsByCompteAndTypeRequest() {}

    public TransactionsByCompteAndTypeRequest(String compteId, Integer typeId) {
        this.compteId = compteId;
        this.typeId = typeId;
    }

    // Getters et Setters
    public String getCompteId() {
        return compteId;
    }

    public void setCompteId(String compteId) {
        this.compteId = compteId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }
}