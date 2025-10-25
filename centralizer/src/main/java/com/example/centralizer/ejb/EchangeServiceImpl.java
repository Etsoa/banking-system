package com.example.centralizer.ejb;

import com.example.centralizer.dto.echange.Echange;
import jakarta.ejb.Stateful;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Stateful
public class EchangeServiceImpl implements Serializable {
    private static final long serialVersionUID = 1L;
    // private static final String SERVER_URL = "http://localhost:8081/echange/api";
    private static final String SERVER_URL = "http://localhost:8081/echange/api";

    
    private Client client;
    private WebTarget target;
    
    public EchangeServiceImpl() {
        this.client = ClientBuilder.newClient();
        this.target = client.target(SERVER_URL);
    }
    
    /**
     * Récupère tous les taux de change
     */
    public List<Echange> getAllEchanges() {
        return target.path("/echanges")
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<Echange>>() {});
    }
    
    /**
     * Récupère les taux de change actifs
     */
    public List<Echange> getEchangesActifs() {
        return target.path("/echanges/actifs")
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<Echange>>() {});
    }
    
    /**
     * Récupère les taux de change actifs à une date donnée
     */
    public List<Echange> getEchangesActifsADate(java.time.LocalDate date) {
        return target.path("/echanges/actifs/" + date.toString())
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<Echange>>() {});
    }
    
    /**
     * Convertit un montant en devise étrangère vers Ariary
     */
    public BigDecimal convertirVersAriary(String devise, BigDecimal montant) {
        Map<String, Object> result = target.path("/echanges/convertir/vers-ariary")
                .queryParam("devise", devise)
                .queryParam("montant", montant.toString())
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<Map<String, Object>>() {});
        
        String montantAriaryStr = result.get("montantAriary").toString();
        return new BigDecimal(montantAriaryStr);
    }
    
    /**
     * Convertit un montant en devise étrangère vers Ariary à une date donnée
     */
    public BigDecimal convertirVersAriaryADate(String devise, BigDecimal montant, java.time.LocalDate date) {
        Map<String, Object> result = target.path("/echanges/convertir/vers-ariary")
                .queryParam("devise", devise)
                .queryParam("montant", montant.toString())
                .queryParam("date", date.toString())
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<Map<String, Object>>() {});
        
        String montantAriaryStr = result.get("montantAriary").toString();
        return new BigDecimal(montantAriaryStr);
    }
    
    /**
     * Convertit un montant en Ariary vers une devise étrangère
     */
    public BigDecimal convertirDepuisAriary(String devise, BigDecimal montantAriary) {
        Map<String, Object> result = target.path("/echanges/convertir/depuis-ariary")
                .queryParam("devise", devise)
                .queryParam("montantAriary", montantAriary.toString())
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<Map<String, Object>>() {});
        
        String montantDeviseStr = result.get("montantDevise").toString();
        return new BigDecimal(montantDeviseStr);
    }
    
    /**
     * Extrait le code devise depuis le nom complet (ex: "EUR/MGA" -> "EUR")
     */
    public String extraireCodeDevise(String nomComplet) {
        if (nomComplet != null && nomComplet.contains("/")) {
            return nomComplet.split("/")[0];
        }
        return nomComplet;
    }
}
