package com.example.centralizer.models.compteDepotDTO;

/**
 * DTO pour les requêtes avec un ID de client
 */
public class ClientRequest {
    private Integer clientId;

    // Constructeurs
    public ClientRequest() {}

    public ClientRequest(Integer clientId) {
        this.clientId = clientId;
    }

    // Getters et Setters
    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }
}